describe('Config Wizard — Step 1: Report Settings',
  { baseUrl: Cypress.env('wizardBaseUrl') },
  () => {
    beforeEach(() => {
      // Suppress any uncaught exceptions from the main app loaded by the global beforeEach.
      cy.on('uncaught:exception', () => false);

      cy.intercept('POST', '/api/validate', { valid: true }).as('validate');
      cy.intercept('POST', '/api/validate-glob', { valid: true }).as('validateGlob');
      cy.intercept('POST', '/api/validate-config', { valid: true }).as('validateConfig');
      cy.intercept('POST', '/api/preview', { yaml: 'title: Test\n' }).as('preview');
      cy.intercept('POST', '/api/generate', {
        success: true,
        path: '/tmp/generated-configs/report-config.yaml',
      }).as('generate');

      cy.visit('/config-wizard');
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
  },
);
