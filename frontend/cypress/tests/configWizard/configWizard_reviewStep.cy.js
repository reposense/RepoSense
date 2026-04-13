function setupIntercepts(overrides = {}) {
  cy.intercept('POST', '/api/validate', overrides.validate || { valid: true }).as('validateRepo');
  cy.intercept('POST', '/api/validate-glob', overrides.validateGlob || { valid: true }).as('validateGlob');
  cy.intercept('POST', '/api/validate-config', overrides.validateConfig || { valid: true }).as('validateConfig');
  cy.intercept('POST', '/api/generate',
    overrides.generate || { success: true, path: '/output/report-config.yaml' }).as('generate');
  const defaultYaml = 'title: RepoSense Report\nrepos:\n'
    + '- repo: https://github.com/test/repo.git\n';
  cy.intercept('POST', '/api/preview',
    overrides.preview || { yaml: defaultYaml }).as('preview');
  cy.intercept('POST', '/api/quit', { statusCode: 200 }).as('quit');
}

function goToStep4() {
  // Step 1 -> Next
  cy.get('.btn-primary').click();
  // Step 2: fill repo URL, then Next
  cy.get('.card .form-input').first().type('https://github.com/test/repo.git');
  cy.get('.card .form-input').first().blur();
  cy.wait('@validateRepo');
  cy.get('.btn-primary').click();
  // Step 3: Skip
  cy.get('.step-heading').should('contain', 'Groups');
  cy.contains('button', 'Skip').click();
  cy.get('.step-heading').should('contain', 'Review & Generate');
}

describe('config wizard review step',
  { baseUrl: Cypress.env('configWizardBaseUrl') },
  () => {
    beforeEach(() => {
      setupIntercepts();
      cy.visit('/config-wizard/');
      goToStep4();
    });

    it('renders step 4 heading', () => {
      cy.get('.step-heading').should('contain', 'Review & Generate');
    });

    it('shows summary card with counts', () => {
      cy.get('.summary-card').should('be.visible');
      cy.get('.summary-row').should('have.length', 4);
      // Should show at least 1 repo
      cy.get('.summary-row').eq(0).find('.summary-label').should('contain', 'Repositories');
      cy.get('.summary-row').eq(0).find('.summary-value').should('contain', '1');
    });

    it('shows preview snippet', () => {
      cy.get('.preview-snippet').should('be.visible');
    });

    it('runs Tier 3 validation on mount and shows valid', () => {
      cy.wait('@validateConfig');
      cy.get('.status-valid').should('contain', 'Configuration is valid');
    });

    it('shows validating state while waiting for response', () => {
      setupIntercepts({
        validateConfig: (req) => {
        // Delay response to observe validating state
          req.reply({ delay: 1000, body: { valid: true } });
        },
      });
      cy.visit('/config-wizard/');
      goToStep4();
      cy.get('.status-validating').should('contain', 'Validating configuration');
    });

    it('invalid config disables Generate button', () => {
      setupIntercepts({ validateConfig: { valid: false, error: 'Parse error in config' } });
      cy.visit('/config-wizard/');
      goToStep4();
      cy.wait('@validateConfig');
      cy.get('.status-invalid').should('contain', 'Validation failed');
      cy.get('.btn-primary').should('be.disabled');
    });

    it('dismiss validation error re-enables Generate', () => {
      setupIntercepts({ validateConfig: { valid: false, error: 'Parse error' } });
      cy.visit('/config-wizard/');
      goToStep4();
      cy.wait('@validateConfig');
      cy.get('.btn-primary').should('be.disabled');
      cy.contains('button', 'Dismiss and generate anyway').click();
      cy.get('.btn-primary').should('not.be.disabled');
    });

    it('successful generate shows success box', () => {
      cy.wait('@validateConfig');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.status-box.success').should('be.visible');
      cy.get('.status-box.success').should('contain', 'generated successfully');
    });

    it('shows generated file path and run command', () => {
      cy.wait('@validateConfig');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.status-path code').should('contain', '/output/report-config.yaml');
      cy.get('.run-command').should('contain', 'java -jar RepoSense.jar --config');
    });

    it('copy command button is visible after success', () => {
      cy.wait('@validateConfig');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.copy-cmd-btn').should('be.visible');
    });

    it('close button triggers quit', () => {
      cy.wait('@validateConfig');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.close-btn').click();
      cy.wait('@quit');
    });

    it('failed generate shows error box', () => {
      setupIntercepts({ generate: { success: false, error: 'Write failed' } });
      cy.visit('/config-wizard/');
      goToStep4();
      cy.wait('@validateConfig');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.status-box.error').should('be.visible');
      cy.get('.status-box.error').should('contain', 'Write failed');
    });

    it('Back goes to step 3', () => {
      cy.contains('button', 'Back').click();
      cy.get('.step-heading').should('contain', 'Groups');
    });
  });
