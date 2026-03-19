describe('Config Wizard — Step 3: Groups',
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
      // Step 1 → Step 2: enter a repo URL and advance.
      cy.contains('Next').click();
      cy.get('.form-input').first()
        .clear()
        .type('https://github.com/reposense/RepoSense.git')
        .blur();
      cy.wait('@validate');
      // Step 2 → Step 3.
      cy.contains('Next').click();
      cy.get('h2.step-heading').should('contain', 'Groups');
    });

    it('displays the Groups step heading', () => {
      cy.get('h2.step-heading').should('contain', 'Groups');
    });

    it('shows a Skip button', () => {
      cy.contains('Skip').should('be.visible');
    });

    it('advances to Step 4 when clicking Skip', () => {
      cy.contains('Skip').click();
      cy.get('h2.step-heading').should('contain', 'Review & Generate');
    });

    it('shows a Back button that returns to Step 2', () => {
      cy.contains('← Back').click();
      cy.get('h2.step-heading').should('contain', 'Repos & Branches');
    });

    it('adds a group when clicking Add Group', () => {
      cy.contains('+ Add Group').click();
      cy.get('.nested-card').should('have.length.at.least', 1);
    });

    it('removes a group when clicking Remove', () => {
      cy.contains('+ Add Group').click();
      cy.get('.nested-card').should('have.length', 1);
      cy.contains('Remove').click();
      cy.get('.nested-card').should('have.length', 0);
    });

    it('shows a glob validation error when an invalid pattern is entered', () => {
      cy.intercept('POST', '/api/validate-glob', {
        valid: false,
        error: 'Unclosed bracket',
      }).as('validateGlobInvalid');

      cy.contains('+ Add Group').click();
      // The glob input is a TagChipInput — type and press Enter to add a tag.
      cy.get('.nested-card .tag-input').first().type('[{enter}');
      cy.wait('@validateGlobInvalid');
      cy.get('.field-error').should('be.visible').and('contain', 'Unclosed bracket');
    });

    it('shows an alert when clicking Next with missing group fields', () => {
      cy.contains('+ Add Group').click();
      // Do not fill in the group name or globs — click Next immediately.
      const alertStub = cy.stub();
      cy.on('window:alert', alertStub);
      cy.contains('Next').click().then(() => {
        expect(alertStub).to.have.been.called;
      });
    });

    it('advances to Step 4 after filling in a valid group and clicking Next', () => {
      cy.contains('+ Add Group').click();
      cy.get('.nested-card .form-input').first().type('frontend');
      // TagChipInput: type a glob pattern and press Enter.
      cy.get('.nested-card .tag-input').first().type('src/**/*.ts{enter}');
      cy.wait('@validateGlob');
      cy.contains('Next').click();
      cy.get('h2.step-heading').should('contain', 'Review & Generate');
    });
  },
);
