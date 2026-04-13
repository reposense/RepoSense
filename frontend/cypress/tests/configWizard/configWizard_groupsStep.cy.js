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

function goToStep3() {
  // Step 1 -> Next
  cy.get('.btn-primary').click();
  // Step 2: fill repo URL, then Next
  cy.get('.card .form-input').first().type('https://github.com/test/repo.git');
  cy.get('.card .form-input').first().blur();
  cy.wait('@validateRepo');
  cy.get('.btn-primary').click();
  cy.get('.step-heading').should('contain', 'Groups');
}

describe('config wizard groups step',
  { baseUrl: Cypress.env('configWizardBaseUrl') },
  () => {
    beforeEach(() => {
      setupIntercepts();
      cy.visit('/config-wizard/');
      goToStep3();
    });

    it('renders step 3 heading', () => {
      cy.get('.step-heading').should('contain', 'Groups');
    });

    it('shows repo card with repo URL', () => {
      cy.get('.card').should('have.length', 1);
    });

    it('shows description text', () => {
      cy.get('.step-description').should('contain', 'Groups classify files');
    });

    it('shows no groups initially', () => {
      cy.get('.empty-hint').should('contain', 'No groups defined');
    });

    it('can add a group', () => {
      cy.get('.add-group-btn').click();
      cy.get('.nested-card').should('have.length', 1);
    });

    it('can enter group name and glob pattern', () => {
      cy.get('.add-group-btn').click();
      cy.get('.nested-card .form-input').first().type('frontend');
      cy.get('.nested-card .form-input').first().should('have.value', 'frontend');
      cy.get('.nested-card .tag-chip-input .chip-input').type('src/frontend/**{enter}');
      cy.get('.nested-card .chip').should('have.length', 1);
    });

    it('validates glob pattern on add', () => {
      cy.get('.add-group-btn').click();
      cy.get('.nested-card .tag-chip-input .chip-input').type('src/**{enter}');
      cy.wait('@validateGlob');
    });

    it('invalid glob shows error', () => {
      setupIntercepts({ validateGlob: { valid: false, error: 'Bad pattern' } });
      cy.get('.add-group-btn').click();
      cy.get('.nested-card .tag-chip-input .chip-input').type('[bad{enter}');
      cy.wait('@validateGlob');
      cy.get('.field-error').should('contain', 'Invalid pattern');
    });

    it('can remove a group', () => {
      cy.get('.add-group-btn').click();
      cy.get('.nested-card').should('have.length', 1);
      cy.get('.nested-card .btn-danger').click();
      cy.get('.nested-card').should('have.length', 0);
    });

    it('Skip clears groups and advances to step 4', () => {
      cy.get('.add-group-btn').click();
      cy.get('.nested-card .form-input').first().type('test-group');
      cy.contains('button', 'Skip').click();
      cy.get('.step-heading').should('contain', 'Review & Generate');
    });

    it('Next with missing group name shows alert', () => {
      cy.on('window:alert', cy.stub().as('alert'));
      cy.get('.add-group-btn').click();
      // Group has no name and no globs
      cy.get('.btn-primary').click();
      cy.get('@alert').should('have.been.calledOnce');
    });

    it('Next with duplicate group names shows alert', () => {
      cy.on('window:alert', cy.stub().as('alert'));
      cy.get('.add-group-btn').click();
      cy.get('.add-group-btn').click();
      // Fill both groups with same name and valid globs
      cy.get('.nested-card').eq(0).find('.form-input').first().type('frontend');
      cy.get('.nested-card').eq(0).find('.tag-chip-input .chip-input').type('a/**{enter}');
      cy.get('.nested-card').eq(1).find('.form-input').first().type('frontend');
      cy.get('.nested-card').eq(1).find('.tag-chip-input .chip-input').type('b/**{enter}');
      cy.get('.btn-primary').click();
      cy.get('@alert').should('have.been.calledOnce');
    });

    it('Back goes to step 2', () => {
      cy.contains('button', 'Back').click();
      cy.get('.step-heading').should('contain', 'Repos & Branches');
    });
  });
