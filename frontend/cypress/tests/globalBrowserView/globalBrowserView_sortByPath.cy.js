describe('global browser view - sort by path', () => {
  beforeEach(() => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');
  });

  it('should have "Sort By Path" button active by default', () => {
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Sort By Path')
      .should('have.class', 'active');

    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .should('not.have.class', 'active');
  });

  it('should display files in a flat list sorted alphabetically by path', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .file-list .file-path')
      .then(($els) => {
        const paths = Cypress._.map($els, (el) => el.textContent.trim());
        const sorted = [...paths].sort((a, b) => a.localeCompare(b));
        expect(paths).to.deep.eq(sorted);
      });
  });

  it('should show repo badge for each file in path view', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.repo-badge')
      .should('exist')
      .and('not.be.empty');
  });

  it('should show file count', () => {
    cy.get('#tab-file-browser .file-count')
      .should('exist')
      .and('contain', 'file(s)');
  });

  it('should show line count for non-binary files', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.line-count')
      .should('exist')
      .invoke('text')
      .should('match', /\d+ lines/);
  });

  it('should switch to Group By Repo view and back', () => {
    // Switch to repo view
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .click();

    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .should('have.class', 'active');

    cy.get('#tab-file-browser .toggle-btn')
      .contains('Sort By Path')
      .should('not.have.class', 'active');

    cy.get('#tab-file-browser .repo-group')
      .should('have.length.greaterThan', 0);

    // Switch back to path view
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Sort By Path')
      .click();

    cy.get('#tab-file-browser .toggle-btn')
      .contains('Sort By Path')
      .should('have.class', 'active');

    cy.get('#tab-file-browser .file-list .file-item')
      .should('have.length.greaterThan', 0);
  });

  it('should collapse all expanded files when switching view mode', () => {
    // Expand a file in path view
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .should('have.class', 'is-expanded');

    // Switch to repo view
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .click();

    cy.get('#tab-file-browser .file-item.is-expanded')
      .should('have.length', 0);

    // Switch back to path view
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Sort By Path')
      .click();

    cy.get('#tab-file-browser .file-item.is-expanded')
      .should('have.length', 0);
  });

  it('should show repo badge matching the correct repo for a known file', () => {
    cy.get('#tab-file-browser .search-input')
      .type('action.yml');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.repo-badge')
      .should('contain', 'reposense/repoSense-action[main]');
  });

  it('should display file path in monospace font', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-path')
      .should('have.css', 'font-family')
      .and('match', /monospace/);
  });
});
