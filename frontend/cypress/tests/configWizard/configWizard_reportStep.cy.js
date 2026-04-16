function setupIntercepts(overrides = {}) {
  cy.intercept('POST', '/api/validate', overrides.validate || { valid: true }).as('validateRepo');
  cy.intercept('POST', '/api/validate-glob', overrides.validateGlob || { valid: true }).as('validateGlob');
  cy.intercept('POST', '/api/validate-config', overrides.validateConfig || { valid: true }).as('validateConfig');
  cy.intercept('POST', '/api/generate',
    overrides.generate || { success: true, path: '/output/report-config.yaml' }).as('generate');
  cy.intercept('POST', '/api/preview',
    overrides.preview || { yaml: 'title: RepoSense Report\nrepos: []\n' }).as('preview');
  cy.intercept('POST', '/api/quit', { statusCode: 200 }).as('quit');
}

describe('config wizard report step',
  { baseUrl: Cypress.env('configWizardBaseUrl') },
  () => {
    beforeEach(() => {
      setupIntercepts();
      cy.visit('/config-wizard/');
    });

    it('renders step 1 heading', () => {
      cy.get('.step-heading').should('contain', 'Report Settings');
    });

    it('report title input has default value', () => {
      cy.get('#report-title').should('have.value', 'RepoSense Report');
    });

    it('can modify report title', () => {
      cy.get('#report-title').clear().type('Custom Report');
      cy.get('#report-title').should('have.value', 'Custom Report');
    });

    it('Next advances to step 2', () => {
      cy.get('.btn-primary').click();
      cy.get('.step-heading').should('contain', 'Repos & Branches');
    });

    it('preserves custom title after navigating back', () => {
      cy.get('#report-title').clear().type('My Custom Title');
      cy.get('.btn-primary').click();

      // Go back to step 1
      cy.get('.step-label').eq(0).click();
      cy.get('#report-title').should('have.value', 'My Custom Title');
    });

    it('YAML preview pane is visible', () => {
      cy.get('.yaml-content').should('be.visible');
    });
  });
