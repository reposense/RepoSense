/**
 * Full happy-path end-to-end flow for the Config Wizard.
 *
 * All backend API calls are mocked with cy.intercept() so the suite is
 * self-contained and does not require a running RepoSense Java server.
 */
describe('Config Wizard — Full Happy-Path Flow',
  { baseUrl: Cypress.env('wizardBaseUrl') },
  () => {
    beforeEach(() => {
      // Suppress any uncaught exceptions from the main app loaded by the global beforeEach.
      cy.on('uncaught:exception', () => false);

      cy.intercept('POST', '/api/validate', { valid: true }).as('validate');
      cy.intercept('POST', '/api/validate-glob', { valid: true }).as('validateGlob');
      cy.intercept('POST', '/api/validate-config', { valid: true }).as('validateConfig');
      cy.intercept('POST', '/api/preview', { yaml: 'title: My Flow Report\n' }).as('preview');
      cy.intercept('POST', '/api/generate', {
        success: true,
        path: '/tmp/generated-configs/report-config.yaml',
      }).as('generate');

      cy.visit('/config-wizard');
    });

    it('completes the full wizard flow from Step 1 to a successful generation', () => {
      // --- Step 1: Report Settings ---
      cy.get('h2.step-heading').should('contain', 'Report Settings');
      cy.get('#report-title').clear().type('My Flow Report');
      cy.contains('Next').click();

      // --- Step 2: Repos & Branches ---
      cy.get('h2.step-heading').should('contain', 'Repos & Branches');
      cy.get('.form-input').first()
        .clear()
        .type('https://github.com/reposense/RepoSense.git')
        .blur();
      cy.wait('@validate');
      cy.get('.field-valid').should('be.visible');
      cy.contains('Next').click();

      // --- Step 3: Groups (skip) ---
      cy.get('h2.step-heading').should('contain', 'Groups');
      cy.contains('Skip').click();

      // --- Step 4: Review & Generate ---
      cy.get('h2.step-heading').should('contain', 'Review & Generate');
      cy.wait('@validateConfig');
      cy.get('.status-valid').should('be.visible');

      cy.contains('Generate Config').click();
      cy.wait('@generate');

      cy.get('.status-box.success').should('be.visible')
        .and('contain', 'report-config.yaml generated successfully');
      cy.get('.run-command').should('contain', 'java -jar RepoSense.jar --config');
    });

    it('allows navigating back to a completed step via the stepper', () => {
      // Advance to Step 2.
      cy.contains('Next').click();
      cy.get('h2.step-heading').should('contain', 'Repos & Branches');

      // Click the Step 1 label in the stepper to go back.
      cy.get('.step-label').first().click();
      cy.get('h2.step-heading').should('contain', 'Report Settings');
    });

    it('shows the YAML preview updating in the right pane as config changes', () => {
      cy.get('#report-title').clear().type('Preview Test Title');
      cy.wait('@preview');
      // The right pane should contain the mocked YAML.
      cy.get('.yaml-content').should('contain', 'title');
    });

    it('completes the flow with a group defined in Step 3', () => {
      // Step 1 → 2
      cy.contains('Next').click();
      cy.get('.form-input').first()
        .clear()
        .type('https://github.com/reposense/RepoSense.git')
        .blur();
      cy.wait('@validate');
      cy.contains('Next').click();

      // Step 3: add a group instead of skipping.
      cy.get('h2.step-heading').should('contain', 'Groups');
      cy.contains('+ Add Group').click();
      cy.get('.nested-card .form-input').first().type('backend');
      cy.get('.nested-card .tag-input').first().type('src/**/*.java{enter}');
      cy.wait('@validateGlob');
      cy.contains('Next').click();

      // Step 4: generate.
      cy.get('h2.step-heading').should('contain', 'Review & Generate');
      cy.wait('@validateConfig');
      cy.contains('Generate Config').click();
      cy.wait('@generate');
      cy.get('.status-box.success').should('be.visible');
    });
  },
);
