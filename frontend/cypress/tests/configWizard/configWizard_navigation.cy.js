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

describe('config wizard navigation',
  { baseUrl: Cypress.env('configWizardBaseUrl') },
  () => {
    beforeEach(() => {
      setupIntercepts();
      cy.visit('/config-wizard/');
    });

    it('wizard app loads at /config-wizard', () => {
      cy.get('#wizard-app').should('exist');
    });

    it('stepper shows 4 steps', () => {
      cy.get('.step-label').should('have.length', 4);
    });

    it('stepper highlights step 1 as active', () => {
      cy.get('.step-label').first().should('have.class', 'active');
    });

    it('completed steps get done class after advancing', () => {
    // Advance to step 2
      cy.get('.btn-primary').click();

      cy.get('.step-label').eq(0).should('have.class', 'done');
      cy.get('.step-label').eq(1).should('have.class', 'active');
    });

    it('clicking a completed step navigates back', () => {
    // Advance to step 2
      cy.get('.btn-primary').click();
      cy.get('.step-heading').should('contain', 'Repos & Branches');

      // Click step 1 in stepper
      cy.get('.step-label').eq(0).click();
      cy.get('.step-heading').should('contain', 'Report Settings');
    });

    it('clicking a future step does nothing', () => {
    // On step 1, click step 3
      cy.get('.step-label').eq(2).click();
      cy.get('.step-heading').should('contain', 'Report Settings');
    });

    it('quit button is visible', () => {
      cy.get('.quit-btn').should('be.visible');
    });

    it('quit button calls /api/quit', () => {
      cy.get('.quit-btn').click();
      cy.wait('@quit');
    });

    it('two-pane layout renders', () => {
      cy.get('.left-pane').should('exist');
      cy.get('.right-pane').should('exist');
      cy.get('.divider').should('exist');
    });

    it('YAML preview shows placeholder initially', () => {
      cy.get('.yaml-content').should('contain', 'Preview will appear here');
    });

    it('copy button is disabled when no preview', () => {
      cy.get('.copy-btn').should('be.disabled');
    });

    it('full happy path end-to-end', () => {
    // Step 1: set title, next
      cy.get('#report-title').clear().type('My Test Report');
      cy.get('.btn-primary').click();

      // Step 2: fill repo URL, next
      cy.get('.card .form-input').first().type('https://github.com/test/repo.git');
      cy.get('.card .form-input').first().blur();
      cy.wait('@validateRepo');
      cy.get('.btn-primary').click();

      // Step 3: skip
      cy.get('.step-heading').should('contain', 'Groups');
      cy.contains('button', 'Skip').click();

      // Step 4: review and generate
      cy.get('.step-heading').should('contain', 'Review & Generate');
      cy.wait('@validateConfig');
      cy.get('.summary-card').should('be.visible');
      cy.get('.btn-primary').click();
      cy.wait('@generate');
      cy.get('.status-box.success').should('be.visible');
    });
  });
