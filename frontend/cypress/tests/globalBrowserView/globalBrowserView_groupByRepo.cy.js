describe('global browser view - group by repo', () => {
  beforeEach(() => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('#tab-file-browser .toggle-btn')
      .contains('Group By Repo')
      .click();
  });

  // Repo group structure
  it('should display repo group headers with name and file count', () => {
    cy.get('#tab-file-browser .repo-group')
      .first()
      .find('.repo-group-name')
      .should('exist');

    cy.get('#tab-file-browser .repo-group')
      .first()
      .find('.repo-group-count')
      .should('contain', 'file(s)');
  });

  it('should have repo groups sorted alphabetically by repo name', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .then(($els) => {
        const names = Cypress._.map($els, (el) => el.textContent.trim());
        const sorted = [...names].sort((a, b) => a.localeCompare(b));
        expect(names).to.deep.eq(sorted);
      });
  });

  it('should display all 4 expected repos', () => {
    const expectedRepos = [
      'reposense/RepoSense-auth-helper[master]',
      'reposense/RepoSense[cypress]',
      'reposense/publish-RepoSense[master]',
      'reposense/repoSense-action[main]',
    ];

    cy.get('#tab-file-browser .repo-group-name')
      .should('have.length', expectedRepos.length);

    cy.get('#tab-file-browser .repo-group-name')
      .then(($els) => {
        const names = Cypress._.map($els, (el) => el.textContent.trim());
        expectedRepos.forEach((repo) => {
          expect(names).to.include(repo);
        });
      });
  });

  // Top 3 file preview (collapsed state)
  it('should show at most 3 files per repo group when collapsed', () => {
    cy.get('#tab-file-browser .repo-group')
      .each(($group) => {
        cy.wrap($group)
          .find('.file-item')
          .should('have.length.at.most', 3);
      });
  });

  it('should show "and N more" link for repos with more than 3 files', () => {
    cy.get('#tab-file-browser .more-files')
      .first()
      .should('contain', 'more file(s)...');
  });

  it('should expand repo group to show all files when "more files" is clicked', () => {
    cy.get('#tab-file-browser .more-files')
      .first()
      .then(($moreLink) => {
        const repoGroup = $moreLink.closest('.repo-group');

        cy.wrap($moreLink).click();

        cy.wrap(repoGroup)
          .find('.file-item')
          .should('have.length.greaterThan', 3);
      });
  });

  // Content-specific tests
  it('should show known files from reposense/publish-RepoSense[master]', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .find('.file-path')
      .then(($els) => {
        const paths = Cypress._.map($els, (el) => el.textContent.trim());
        expect(paths).to.include('.gitignore');
        expect(paths).to.include('get-reposense.py');
        expect(paths).to.include('config/deploy.sh');
      });
  });

  it('should show known files from reposense/RepoSense-auth-helper[master]', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/RepoSense-auth-helper[master]')
      .closest('.repo-group')
      .find('.file-path')
      .then(($els) => {
        const paths = Cypress._.map($els, (el) => el.textContent.trim());
        expect(paths).to.include('package-lock.json');
        expect(paths).to.include('package.json');
      });
  });

  it('should show all 9 files of reposense/repoSense-action[main] after clicking "more files"', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/repoSense-action[main]')
      .closest('.repo-group')
      .as('actionGroup');

    cy.get('@actionGroup')
      .find('.file-item')
      .should('have.length', 3);

    cy.get('@actionGroup')
      .find('.more-files')
      .should('contain', 'and 6 more file(s)...')
      .click();

    cy.get('@actionGroup')
      .find('.file-item')
      .should('have.length', 9);

    cy.get('@actionGroup')
      .find('.file-path')
      .then(($els) => {
        const paths = Cypress._.map($els, (el) => el.textContent.trim());
        expect(paths).to.include('action.yml');
        expect(paths).to.include('.github/workflows/test.yml');
        expect(paths).to.include('configs/author-config.csv');
        expect(paths).to.include('configs/repo-config.csv');
        expect(paths).to.include('get-reposense.py');
        expect(paths).to.include('run.sh');
      });

    cy.get('@actionGroup')
      .find('.more-files')
      .should('not.exist');
  });

  // toggleAllFiles — repo group header click
  it('should expand all files when repo group header is clicked', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .as('publishGroup');

    cy.get('@publishGroup')
      .find('.file-item.is-expanded')
      .should('have.length', 0);

    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.file-item')
      .each(($item) => {
        cy.wrap($item).should('have.class', 'is-expanded');
      });
  });

  it('should render file content for all files when repo header is clicked', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .as('publishGroup');

    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    // Some files may be off-screen due to overflow-y: auto, so use 'exist'
    cy.get('@publishGroup')
      .find('.file-item')
      .each(($item) => {
        cy.wrap($item)
          .find('.file-content')
          .should('exist');
      });

    cy.get('@publishGroup')
      .find('.file-item')
      .first()
      .find('.segment')
      .should('have.length.greaterThan', 0);
  });

  it('should collapse all files when repo group header is clicked again', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .as('publishGroup');

    // Expand
    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.file-item.is-expanded')
      .should('have.length.greaterThan', 0);

    // Collapse
    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.file-item.is-expanded')
      .should('have.length', 0);

    cy.get('@publishGroup')
      .find('.file-content')
      .should('not.exist');
  });

  it('should toggle badge text between "Click to expand" and "Click to collapse"', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .as('publishGroup');

    cy.get('@publishGroup')
      .find('.repo-group-is-show-top-badge')
      .should('contain', 'Click to expand');

    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.repo-group-is-show-top-badge')
      .should('contain', 'Click to collapse');

    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.repo-group-is-show-top-badge')
      .should('contain', 'Click to expand');
  });

  it('should reveal all files (not just top 3) when header is clicked on a large repo', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/repoSense-action[main]')
      .closest('.repo-group')
      .as('actionGroup');

    cy.get('@actionGroup')
      .find('.file-item')
      .should('have.length', 3);

    cy.get('@actionGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@actionGroup')
      .find('.file-item')
      .should('have.length', 9);

    cy.get('@actionGroup')
      .find('.file-item')
      .each(($item) => {
        cy.wrap($item).should('have.class', 'is-expanded');
      });

    cy.get('@actionGroup')
      .find('.more-files')
      .should('not.exist');
  });

  it('should show segments with author border colors when repo header expands files', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/RepoSense-auth-helper[master]')
      .closest('.repo-group')
      .as('authHelperGroup');

    cy.get('@authHelperGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@authHelperGroup')
      .find('.file-item')
      .first()
      .find('.segment')
      .should('have.length.greaterThan', 0);

    cy.get('@authHelperGroup')
      .find('.file-item')
      .first()
      .find('.segment')
      .first()
      .should('have.css', 'border-left-style', 'solid');
  });

  it('should only expand files of the clicked repo, not other repos', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .find('.repo-group-header')
      .click();

    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .find('.file-item.is-expanded')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/RepoSense-auth-helper[master]')
      .closest('.repo-group')
      .find('.file-item.is-expanded')
      .should('have.length', 0);
  });

  it('should expand and collapse multiple repo groups independently', () => {
    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/publish-RepoSense[master]')
      .closest('.repo-group')
      .as('publishGroup');

    cy.get('#tab-file-browser .repo-group-name')
      .contains('reposense/RepoSense-auth-helper[master]')
      .closest('.repo-group')
      .as('authHelperGroup');

    // Expand both
    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@authHelperGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.file-item.is-expanded')
      .should('have.length.greaterThan', 0);

    cy.get('@authHelperGroup')
      .find('.file-item.is-expanded')
      .should('have.length.greaterThan', 0);

    // Collapse only publish
    cy.get('@publishGroup')
      .find('.repo-group-header')
      .click();

    cy.get('@publishGroup')
      .find('.file-item.is-expanded')
      .should('have.length', 0);

    cy.get('@authHelperGroup')
      .find('.file-item.is-expanded')
      .should('have.length.greaterThan', 0);
  });

  // Individual file expansion within repo view
  it('should expand individual file by clicking file header without affecting others', () => {
    cy.get('#tab-file-browser .repo-group')
      .first()
      .find('.file-item')
      .first()
      .as('firstFile');

    cy.get('@firstFile')
      .find('.file-header')
      .click();

    cy.get('@firstFile')
      .should('have.class', 'is-expanded');

    cy.get('@firstFile')
      .find('.file-content')
      .scrollIntoView()
      .should('be.visible');

    cy.get('#tab-file-browser .repo-group')
      .first()
      .find('.file-item')
      .eq(1)
      .should('not.have.class', 'is-expanded');
  });
});
