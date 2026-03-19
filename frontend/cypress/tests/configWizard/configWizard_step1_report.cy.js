// The wizard runs on a separate Vite server (port 9002). We use absolute URLs for visits
// and intercepts so that the global support.js beforeEach (which visits localhost:9000)
// is unaffected by these tests.
const WIZARD_BASE = Cypress.env('wizardBaseUrl'); // 'http://localhost:9002'

describe('Config Wizard — Step 1: Report Settings', () => {
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
  });

  it('displays the Report Settings step heading', () => {
    cy.get('h2.step-heading').should('contain', 'Report Settings');
  });

  it('shows the report title input with a default value', () => {
    cy.get('#report-title').should('exist');
    cy.get('#report-title').should('not.have.value', '');
  });

  it('advances to Step 2 after clicking Next with the default title', () => {
    cy.contains('Next').click();
    cy.get('h2.step-heading').should('contain', 'Repos & Branches');
  });

  it('advances to Step 2 after entering a custom title and clicking Next', () => {
    cy.get('#report-title').clear().type('My Custom Report');
    cy.contains('Next').click();
    cy.get('h2.step-heading').should('contain', 'Repos & Branches');
  });

  it('marks Step 1 as done in the stepper after advancing', () => {
    cy.contains('Next').click();
    cy.get('.step-label').first().should('have.class', 'done');
  });

  it('marks Step 2 as active in the stepper after advancing', () => {
    cy.contains('Next').click();
    cy.get('.step-label').eq(1).should('have.class', 'active');
  });
});
