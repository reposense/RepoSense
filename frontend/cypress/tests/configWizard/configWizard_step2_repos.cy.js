// The wizard runs on a separate Vite server (port 9002). We use absolute URLs for visits
// and intercepts so that the global support.js beforeEach (which visits localhost:9000)
// is unaffected by these tests.
const WIZARD_BASE = Cypress.env('wizardBaseUrl'); // 'http://localhost:9002'

describe('Config Wizard — Step 2: Repos & Branches', () => {
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

    cy.visit(`${WIZARD_BASE}/config-wizard`);
    // Navigate to Step 2.
    cy.contains('Next').click();
    cy.get('h2.step-heading').should('contain', 'Repos & Branches');
  });

  it('displays the Repos & Branches step heading', () => {
    cy.get('h2.step-heading').should('contain', 'Repos & Branches');
  });

  it('shows a repository URL input field', () => {
    cy.get('.form-input').first().should('exist');
  });

  it('shows a Back button that returns to Step 1', () => {
    cy.contains('← Back').click();
    cy.get('h2.step-heading').should('contain', 'Report Settings');
  });

  it('shows a valid indicator after entering a valid repo URL and blurring', () => {
    cy.get('.form-input').first()
      .clear()
      .type('https://github.com/reposense/RepoSense.git')
      .blur();
    cy.wait('@validate');
    cy.get('.field-valid').should('be.visible');
  });

  it('shows an error indicator after entering an invalid repo URL and blurring', () => {
    cy.intercept('POST', `${WIZARD_BASE}/api/validate`, {
      valid: false,
      error: 'Invalid location',
    }).as('validateInvalid');

    cy.get('.form-input').first()
      .clear()
      .type('not-a-valid-url')
      .blur();
    cy.wait('@validateInvalid');
    cy.get('.field-error').should('be.visible').and('contain', 'Invalid location');
  });

  it('adds a second repository when clicking Add Repository', () => {
    cy.contains('+ Add Repository').click();
    cy.get('.card').should('have.length', 2);
  });

  it('removes a repository card when clicking Remove', () => {
    cy.contains('+ Add Repository').click();
    cy.get('.card').should('have.length', 2);
    cy.get('.btn-danger').first().click();
    cy.get('.card').should('have.length', 1);
  });

  it('adds a branch when clicking Add Branch', () => {
    cy.contains('+ Add Branch').click();
    // The default repo has 1 branch; adding one gives 2.
    cy.get('.branch-card').should('have.length.at.least', 2);
  });

  it('advances to Step 3 after entering a valid URL and clicking Next', () => {
    cy.get('.form-input').first()
      .clear()
      .type('https://github.com/reposense/RepoSense.git')
      .blur();
    cy.wait('@validate');
    cy.contains('Next').click();
    cy.get('h2.step-heading').should('contain', 'Groups');
  });
});
