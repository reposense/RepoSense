// The wizard runs on a separate Vite server (port 9002). We use absolute URLs for visits
// and intercepts so that the global support.js beforeEach (which visits localhost:9000)
// is unaffected by these tests.
const WIZARD_BASE = Cypress.env('wizardBaseUrl'); // 'http://localhost:9002'

describe('Config Wizard — Step 4: Review & Generate', () => {
  /**
   * Navigate through Steps 1–3 and arrive at Step 4.
   * All API calls are mocked so tests are self-contained and CI-safe.
   */
  const navigateToStep4 = () => {
    cy.visit(`${WIZARD_BASE}/config-wizard`);
    // Step 1 → 2
    cy.contains('Next').click();
    // Step 2: enter a valid repo URL, then advance.
    cy.get('.form-input').first()
      .clear()
      .type('https://github.com/reposense/RepoSense.git')
      .blur();
    cy.wait('@validate');
    cy.contains('Next').click();
    // Step 3: skip groups.
    cy.contains('Skip').click();
    cy.get('h2.step-heading').should('contain', 'Review & Generate');
  };

  beforeEach(() => {
    // Suppress any uncaught exceptions from the wizard app (e.g. missing report data).
    cy.on('uncaught:exception', () => false);

    cy.intercept('POST', `${WIZARD_BASE}/api/validate`, { valid: true }).as('validate');
    cy.intercept('POST', `${WIZARD_BASE}/api/validate-glob`, { valid: true }).as('validateGlob');
    cy.intercept('POST', `${WIZARD_BASE}/api/validate-config`, { valid: true }).as('validateConfig');
    cy.intercept('POST', `${WIZARD_BASE}/api/preview`, { yaml: 'title: Test\n' }).as('preview');
    cy.intercept('POST', `${WIZARD_BASE}/api/generate`, {
      success: true,
      path: '/tmp/generated-configs/report-config.yaml',
    }).as('generate');

    navigateToStep4();
  });

  it('displays the Review & Generate step heading', () => {
    cy.get('h2.step-heading').should('contain', 'Review & Generate');
  });

  it('shows a config summary with at least one repository', () => {
    cy.get('.summary-row').should('have.length.at.least', 1);
  });

  it('shows a YAML preview snippet', () => {
    cy.get('.preview-snippet').should('exist');
  });

  it('shows a validation status after mounting', () => {
    cy.wait('@validateConfig');
    cy.get('.status-valid').should('be.visible').and('contain', '✓ Configuration is valid');
  });

  it('shows the Generate Config button', () => {
    cy.contains('Generate Config').should('exist');
  });

  it('shows a Back button that returns to Step 3', () => {
    cy.contains('← Back').click();
    cy.get('h2.step-heading').should('contain', 'Groups');
  });

  it('shows a success message after clicking Generate Config', () => {
    cy.wait('@validateConfig');
    cy.contains('Generate Config').click();
    cy.wait('@generate');
    cy.get('.status-box.success').should('be.visible');
    cy.get('.status-box.success').should('contain', 'report-config.yaml generated successfully');
  });

  it('shows the generated file path after successful generation', () => {
    cy.wait('@validateConfig');
    cy.contains('Generate Config').click();
    cy.wait('@generate');
    cy.get('.status-path code').should('contain', 'report-config.yaml');
  });

  it('shows the next-steps run command after successful generation', () => {
    cy.wait('@validateConfig');
    cy.contains('Generate Config').click();
    cy.wait('@generate');
    cy.get('.run-command').should('contain', 'java -jar RepoSense.jar --config');
  });

  it('copies the run command to clipboard when clicking Copy command', () => {
    cy.wait('@validateConfig');
    cy.contains('Generate Config').click();
    cy.wait('@generate');
    cy.window().then((win) => {
      cy.stub(win.navigator.clipboard, 'writeText').as('clipboardWrite');
    });
    cy.contains('Copy command').click();
    cy.get('@clipboardWrite').should('have.been.calledWith',
      Cypress.sinon.match(/java -jar RepoSense.jar --config/));
  });

  it('shows a validation failure message when Tier 3 validation fails', () => {
    // Override the validate-config intercept for this test only.
    cy.intercept('POST', `${WIZARD_BASE}/api/validate-config`, {
      valid: false,
      error: 'Missing required field: repo',
    }).as('validateConfigFail');

    cy.visit(`${WIZARD_BASE}/config-wizard`);
    cy.contains('Next').click();
    cy.get('.form-input').first()
      .clear()
      .type('https://github.com/reposense/RepoSense.git')
      .blur();
    cy.wait('@validate');
    cy.contains('Next').click();
    cy.contains('Skip').click();

    cy.wait('@validateConfigFail');
    cy.get('.status-invalid').should('be.visible')
      .and('contain', 'Missing required field: repo');
  });

  it('disables the Generate Config button while validation is in progress', () => {
    // Delay the validate-config response to catch the transient disabled state.
    cy.intercept('POST', `${WIZARD_BASE}/api/validate-config`, (req) => {
      req.reply({ delay: 1000, body: { valid: true } });
    }).as('validateConfigDelayed');

    cy.visit(`${WIZARD_BASE}/config-wizard`);
    cy.contains('Next').click();
    cy.get('.form-input').first()
      .clear()
      .type('https://github.com/reposense/RepoSense.git')
      .blur();
    cy.wait('@validate');
    cy.contains('Next').click();
    cy.contains('Skip').click();

    // Generate Config button should be disabled while validation is still running.
    cy.contains('Generate Config').should('be.disabled');
    cy.wait('@validateConfigDelayed');
    cy.contains('Generate Config').should('not.be.disabled');
  });
});
