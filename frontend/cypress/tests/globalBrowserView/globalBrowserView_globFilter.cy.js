describe('global browser view - glob filter', () => {
  beforeEach(() => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');
  });

  it('should filter files using glob pattern', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .its('length')
      .then((initialCount) => {
        cy.get('#tab-file-browser .search-input')
          .type('*.java');

        cy.get('#tab-file-browser .file-list .file-item')
          .should('have.length.lessThan', initialCount);

        cy.get('#tab-file-browser .file-list .file-path')
          .each(($el) => {
            expect($el.text().trim()).to.match(/\.java$/);
          });
      });
  });

  it('should show empty state when no files match filter', () => {
    cy.get('#tab-file-browser .search-input')
      .type('nonexistentpattern12345');

    cy.get('#tab-file-browser .empty-state')
      .should('be.visible')
      .and('contain', 'No files match the current filter');
  });

  it('should update file count when filter is applied', () => {
    cy.get('#tab-file-browser .file-count')
      .invoke('text')
      .then((initialText) => {
        cy.get('#tab-file-browser .search-input')
          .type('*.java');

        cy.get('#tab-file-browser .file-count')
          .invoke('text')
          .should('not.eq', initialText);
      });
  });

  it('should show all files again when filter is cleared', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .its('length')
      .then((initialCount) => {
        cy.get('#tab-file-browser .search-input')
          .type('*.java');

        cy.get('#tab-file-browser .file-list .file-item')
          .should('have.length.lessThan', initialCount);

        cy.get('#tab-file-browser .search-input')
          .clear();

        cy.get('#tab-file-browser .file-list .file-item')
          .should('have.length', initialCount);
      });
  });

  it('should filter to only .py files across all repos', () => {
    cy.get('#tab-file-browser .search-input')
      .type('*.py');

    cy.get('#tab-file-browser .file-list .file-path')
      .each(($el) => {
        expect($el.text().trim()).to.match(/\.py$/);
      });

    // get-reposense.py exists in both publish-RepoSense and repoSense-action
    cy.get('#tab-file-browser .file-list .file-path')
      .then(($els) => {
        const paths = Cypress._.map($els, (el) => el.textContent.trim());
        expect(paths).to.include('get-reposense.py');
      });
  });

  it('should filter .java files and show only RepoSense[cypress] repo files', () => {
    cy.get('#tab-file-browser .search-input')
      .type('*.java');

    cy.get('#tab-file-browser .file-list .file-item')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .file-list .file-item .repo-badge')
      .each(($el) => {
        expect($el.text().trim()).to.eq('reposense/RepoSense[cypress]');
      });
  });

  it('should filter files in Group By Repo view', () => {
    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .click();

    cy.get('#tab-file-browser .search-input')
      .type('*.java');

    // Only RepoSense[cypress] has .java files, so only 1 repo group should show
    cy.get('#tab-file-browser .repo-group')
      .should('have.length', 1);

    cy.get('#tab-file-browser .repo-group-name')
      .first()
      .should('contain', 'reposense/RepoSense[cypress]');
  });

  it('should filter by directory glob pattern', () => {
    cy.get('#tab-file-browser .search-input')
      .type('configs/*');

    cy.get('#tab-file-browser .file-list .file-path')
      .each(($el) => {
        expect($el.text().trim()).to.match(/^configs\//);
      });
  });

  it('should filter by exact file name', () => {
    cy.get('#tab-file-browser .search-input')
      .type('action.yml');

    cy.get('#tab-file-browser .file-list .file-item')
      .should('have.length', 1);

    cy.get('#tab-file-browser .file-list .file-path')
      .first()
      .should('contain', 'action.yml');
  });

  it('should match files with double-star glob pattern', () => {
    cy.get('#tab-file-browser .search-input')
      .type('**/*.json');

    cy.get('#tab-file-browser .file-list .file-path')
      .each(($el) => {
        expect($el.text().trim()).to.match(/\.json$/);
      });
  });
});
