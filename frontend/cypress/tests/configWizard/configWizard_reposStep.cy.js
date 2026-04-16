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

function goToStep2() {
  cy.get('.btn-primary').click();
  cy.get('.step-heading').should('contain', 'Repos & Branches');
}

describe('config wizard repos step',
  { baseUrl: Cypress.env('configWizardBaseUrl') },
  () => {
    beforeEach(() => {
      setupIntercepts();
      cy.visit('/config-wizard/');
      goToStep2();
    });

    it('renders step 2 with one empty repo card', () => {
      cy.get('.card').should('have.length', 1);
      cy.get('.card-title').should('contain', 'Repository #1');
    });

    it('can enter repo URL', () => {
      cy.get('.card .form-input').first()
        .type('https://github.com/test/repo.git');
      cy.get('.card .form-input').first()
        .should('have.value', 'https://github.com/test/repo.git');
    });

    it('validates repo URL on blur with valid result', () => {
      cy.get('.card .form-input').first()
        .type('https://github.com/test/repo.git')
        .blur();
      cy.wait('@validateRepo');
      cy.get('.card .form-input').first().should('have.class', 'is-valid');
      cy.get('.field-valid').should('contain', 'Valid repository location');
    });

    it('shows error for invalid repo URL', () => {
      setupIntercepts({ validate: { valid: false, error: 'Repository not found' } });
      cy.get('.card .form-input').first()
        .type('https://github.com/invalid/repo.git')
        .blur();
      cy.wait('@validateRepo');
      cy.get('.card .form-input').first().should('have.class', 'is-invalid');
      cy.get('.field-error').should('contain', 'Repository not found');
    });

    it('can add a second repo', () => {
      cy.get('.add-repo-btn').click();
      cy.get('.card').should('have.length', 2);
      cy.get('.card-title').eq(1).should('contain', 'Repository #2');
    });

    it('can remove a repo when multiple exist', () => {
      cy.get('.add-repo-btn').click();
      cy.get('.card').should('have.length', 2);
      cy.get('.card').first().find('.btn-danger').click();
      cy.get('.card').should('have.length', 1);
    });

    it('does not show remove button for single repo', () => {
      cy.get('.card').should('have.length', 1);
      cy.get('.card .btn-danger').should('not.exist');
    });

    it('can add a branch', () => {
      cy.get('.nested-card').then(($cards) => {
        const initialCount = $cards.length;
        cy.get('.add-branch-btn').click();
        cy.get('.nested-card').should('have.length', initialCount + 1);
      });
    });

    it('can remove a branch when multiple exist', () => {
      cy.get('.add-branch-btn').click();
      cy.get('.nested-card').should('have.length', 2);
      cy.get('.nested-card').last().find('.btn-danger').click();
      cy.get('.nested-card').should('have.length', 1);
    });

    it('branch name with spaces shows error', () => {
      cy.get('.nested-card .form-input').first()
        .type('my branch');
      cy.get('.nested-card .form-input').first()
        .should('have.class', 'is-invalid');
      cy.get('.field-error').should('contain', 'Branch name cannot contain spaces');
    });

    it('can set since and until dates', () => {
      cy.get('input[type="date"]').first().type('2024-01-01');
      cy.get('input[type="date"]').first().should('have.value', '2024-01-01');
      cy.get('input[type="date"]').eq(1).type('2024-12-31');
      cy.get('input[type="date"]').eq(1).should('have.value', '2024-12-31');
    });

    it('shows date range error when since is after until', () => {
      cy.get('input[type="date"]').first().type('2024-12-31');
      cy.get('input[type="date"]').eq(1).type('2024-01-01');
      cy.get('.field-error').should('contain', 'Since date must be on or before until date');
    });

    it('can add ignore glob chips', () => {
      cy.get('.tag-chip-input').first().find('.chip-input')
        .type('node_modules/**{enter}');
      cy.get('.tag-chip-input').first().find('.chip')
        .should('have.length', 1)
        .and('contain', 'node_modules/**');
    });

    it('validates glob pattern on chip add', () => {
      cy.get('.tag-chip-input').first().find('.chip-input')
        .type('src/**{enter}');
      cy.wait('@validateGlob');
    });

    it('invalid glob shows error', () => {
      setupIntercepts({ validateGlob: { valid: false, error: 'Invalid glob syntax' } });
      cy.get('.tag-chip-input').first().find('.chip-input')
        .type('[invalid{enter}');
      cy.wait('@validateGlob');
      cy.get('.field-error').should('contain', 'Invalid');
    });

    it('can remove a glob chip', () => {
      cy.get('.tag-chip-input').first().find('.chip-input')
        .type('src/**{enter}');
      cy.get('.tag-chip-input').first().find('.chip').should('have.length', 1);
      cy.get('.tag-chip-input').first().find('.chip-remove').click();
      cy.get('.tag-chip-input').first().find('.chip').should('have.length', 0);
    });

    it('can add an author', () => {
      cy.contains('button', '+ Add Author').click();
      cy.get('.author-card').should('have.length', 1);
    });

    it('author git ID with spaces shows error', () => {
      cy.contains('button', '+ Add Author').click();
      cy.get('.author-card .form-input').first().type('bad id');
      cy.get('.author-card .field-error').should('contain', 'Git Host ID cannot contain spaces');
    });

    it('can remove an author', () => {
      cy.contains('button', '+ Add Author').click();
      cy.get('.author-card').should('have.length', 1);
      cy.get('.author-card .btn-danger').click();
      cy.get('.author-card').should('have.length', 0);
    });

    it('Back goes to step 1', () => {
      cy.contains('button', 'Back').click();
      cy.get('.step-heading').should('contain', 'Report Settings');
    });

    it('Next with empty repo URL shows alert', () => {
      cy.on('window:alert', cy.stub().as('alert'));
      cy.get('.btn-primary').click();
      cy.get('@alert').should('have.been.calledOnce');
    });
  });
