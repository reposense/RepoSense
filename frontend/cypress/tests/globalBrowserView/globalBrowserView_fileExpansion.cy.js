describe('global browser view - file expansion and segment rendering', () => {
  beforeEach(() => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');
  });

  // Toggle open/close
  it('should toggle a file open and closed on consecutive clicks', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .as('firstFile');

    cy.get('@firstFile')
      .should('not.have.class', 'is-expanded');

    cy.get('@firstFile')
      .find('.file-header')
      .click();

    cy.get('@firstFile')
      .should('have.class', 'is-expanded');

    cy.get('@firstFile')
      .find('.file-content')
      .should('be.visible');

    cy.get('@firstFile')
      .find('.file-header')
      .click();

    cy.get('@firstFile')
      .should('not.have.class', 'is-expanded');

    cy.get('@firstFile')
      .find('.file-content')
      .should('not.exist');
  });

  it('should show caret-right when collapsed and caret-down when expanded', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .as('firstFile');

    cy.get('@firstFile')
      .find('.caret svg[data-icon="caret-right"]')
      .should('exist');

    cy.get('@firstFile')
      .find('.file-header')
      .click();

    cy.get('@firstFile')
      .find('.caret svg[data-icon="caret-down"]')
      .should('exist');
  });

  it('should expand multiple files independently', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .eq(0)
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .eq(1)
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .eq(0)
      .should('have.class', 'is-expanded');

    cy.get('#tab-file-browser .file-list .file-item')
      .eq(1)
      .should('have.class', 'is-expanded');

    // Collapse first only
    cy.get('#tab-file-browser .file-list .file-item')
      .eq(0)
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .eq(0)
      .should('not.have.class', 'is-expanded');

    cy.get('#tab-file-browser .file-list .file-item')
      .eq(1)
      .should('have.class', 'is-expanded');
  });

  it('should highlight expanded file with blue border', () => {
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .should('have.class', 'is-expanded')
      .and('have.css', 'border-color', 'rgb(0, 123, 255)');
  });

  // Segment rendering
  it('should render segments with line numbers when file is expanded', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-content')
      .should('be.visible');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.line-number')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.line-number')
      .first()
      .invoke('text')
      .should('contain', '1');
  });

  it('should render segment with author color as 4px solid border-left', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment')
      .first()
      .should('have.css', 'border-left-style', 'solid')
      .and('have.css', 'border-left-width', '4px');
  });

  it('should render code lines with background color for authored segments', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment .code')
      .first()
      .should('have.css', 'background-color');
  });

  it('should show segment title with author name on hover', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment')
      .first()
      .should('have.attr', 'title')
      .and('match', /Author|Co-author/);
  });

  it('should show line content inside code blocks', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.line-content')
      .should('have.length.greaterThan', 0);

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.line-content')
      .first()
      .invoke('text')
      .should('not.be.empty');
  });

  it('should render all segments as open (no collapsed untouched segments)', () => {
    cy.get('#tab-file-browser .search-input')
      .type('get-reposense.py');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    // In global file browser, all segments have knownAuthor set,
    // so none should have the "untouched" class
    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment.untouched')
      .should('have.length', 0);
  });

  it('should load and render segments for a known file from a specific repo', () => {
    cy.get('#tab-file-browser .search-input')
      .type('action.yml');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.repo-badge')
      .should('contain', 'reposense/repoSense-action[main]');

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.file-header')
      .click();

    cy.get('#tab-file-browser .file-list .file-item')
      .first()
      .find('.segment')
      .should('have.length.greaterThan', 0);
  });
});
