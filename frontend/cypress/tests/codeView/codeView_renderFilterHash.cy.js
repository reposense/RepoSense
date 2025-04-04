describe('render filter hash', () => {
  it('filter files: url params should persist after change and reload', () => {
    cy.get('div.mui-textfield.tooltip.filter_file > input:visible')
      .should('be.visible')
      .invoke('val')
      .should('eq', '');

    cy.get('div.mui-textfield.tooltip.filter_file > input:visible')
      .should('be.visible')
      .type('**java**{enter}');

    cy.url()
      .should('contain', 'filteredFileName=**java**');

    cy.reload();

    cy.url()
      .should('contain', 'filteredFileName=**java**');
  })

  it('search: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .invoke('val')
      .should('eq', '');

    /* Enter search and test URL before and after reload */
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugene{enter}');

    cy.url()
      .should('contain', 'search=eugene');

    cy.reload();

    cy.url()
      .should('contain', 'search=eugene');
  });

  it('group by: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('div.mui-select.grouping > select:visible')
      .invoke('val')
      .should('eq', 'groupByRepos');

    cy.url()
      .should('contain', 'groupSelect=groupByRepos');

    /* Select group by none and test URL before and after reload */
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByNone');

    cy.url()
      .should('contain', 'groupSelect=groupByNone');

    cy.reload();

    cy.url()
      .should('contain', 'groupSelect=groupByNone');

    /* Select group by authors and test URL before and after reload */
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors');

    cy.url()
      .should('contain', 'groupSelect=groupByAuthors');

    cy.reload();

    cy.url()
      .should('contain', 'groupSelect=groupByAuthors');
  });

  it('sort groups by: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('div.mui-select.sort-group > select:visible')
      .invoke('val')
      .should('eq', 'groupTitle dsc');

    cy.url()
      .should('contain', 'sort=groupTitle%20dsc');

    /* Select sort by group title ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-group > select:visible')
      .select('groupTitle');

    cy.url()
      .should('contain', 'sort=groupTitle');

    cy.reload();

    cy.url()
      .should('contain', 'sort=groupTitle');

    /* Select sort by contribution descending and test URL before and after reload */
    cy.get('div.mui-select.sort-group > select:visible')
      .select('totalCommits dsc');

    cy.url()
      .should('contain', 'sort=totalCommits%20dsc');

    cy.reload();

    cy.url()
      .should('contain', 'sort=totalCommits%20dsc');

    /* Select sort by contribution ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-group > select:visible')
      .select('totalCommits');

    cy.url()
      .should('contain', 'sort=totalCommits');

    cy.reload();

    cy.url()
      .should('contain', 'sort=totalCommits');

    /* Select sort by variance descending and test URL before and after reload */
    cy.get('div.mui-select.sort-group > select:visible')
      .select('variance dsc');

    cy.url()
      .should('contain', 'sort=variance%20dsc');

    cy.reload();

    cy.url()
      .should('contain', 'sort=variance%20dsc');

    /* Select sort by variance ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-group > select:visible')
      .select('variance');

    cy.url()
      .should('contain', 'sort=variance');

    cy.reload();

    cy.url()
      .should('contain', 'sort=variance');
  });

  it('sort within groups by: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .invoke('val')
      .should('eq', 'title');

    cy.url()
      .should('contain', 'sortWithin=title');

    /* Select sort by group title ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('title dsc');

    cy.url()
      .should('contain', 'sortWithin=title');

    cy.reload();

    cy.url()
      .should('contain', 'sortWithin=title');

    /* Select sort by contribution descending and test URL before and after reload */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('totalCommits dsc');

    cy.url()
      .should('contain', 'sortWithin=totalCommits%20dsc');

    cy.reload();

    cy.url()
      .should('contain', 'sortWithin=totalCommits%20dsc');

    /* Select sort by contribution ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('totalCommits');

    cy.url()
      .should('contain', 'sortWithin=totalCommits');

    cy.reload();

    cy.url()
      .should('contain', 'sortWithin=totalCommits');

    /* Select sort by variance descending and test URL before and after reload */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('variance dsc');

    cy.url()
      .should('contain', 'sortWithin=variance%20dsc');

    cy.reload();

    cy.url()
      .should('contain', 'sortWithin=variance%20dsc');

    /* Select sort by variance ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('variance');

    cy.url()
      .should('contain', 'sortWithin=variance');

    cy.reload();

    cy.url()
      .should('contain', 'sortWithin=variance');
  });

  it('granularity: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('div.mui-select.granularity > select:visible')
      .invoke('val')
      .should('eq', 'commit');

    cy.url()
      .should('contain', 'timeframe=commit');

    /* Select timeframe as day and test URL before and after reload */
    cy.get('div.mui-select.granularity > select:visible')
      .select('day');

    cy.url()
      .should('contain', 'timeframe=day');

    cy.reload();

    cy.url()
      .should('contain', 'timeframe=day');

    /* Select timeframe as week and test URL before and after reload */
    cy.get('div.mui-select.granularity > select:visible')
      .select('week');

    cy.url()
      .should('contain', 'timeframe=week');

    cy.reload();

    cy.url()
      .should('contain', 'timeframe=week');
  });

  it('since: url params should persist after change and reload', () => {
    /* Check initial state */
    cy.get('input[name="since"]:visible')
      .invoke('val')
      .should('eq', '2018-05-03');

    cy.url()
      .should('contain', 'since=2018-05-03');

    /* Modify since date and test URL before and after reload */
    cy.get('input[name="since"]:visible')
      .type('2019-06-04');

    cy.url()
      .should('contain', 'since=2019-06-04');

    cy.reload();

    cy.url()
      .should('contain', 'since=2019-06-04');
  });

  it('until: url params should persist after change and reload', () => {
    /* Check initial state (will require dayjs for getting current date) */
    // cy.get('input[name="until"]:visible')
    //   .invoke('val')
    //   .should('eq', dayjs().format('YYYY-MM-DD'));

    // cy.url()
    //   .should('contain', 'date=2023-07-06');

    /* Modify since date and test URL before and after reload */
    cy.get('input[name="until"]:visible')
      .type('2019-06-04');

    cy.url()
      .should('contain', 'until=2019-06-04');

    cy.reload();

    cy.url()
      .should('contain', 'until=2019-06-04');
  });

  it('breakdown by file type: url params should persist after change and reload', () => {
    cy.get('#summary label.filter-breakdown input:visible')
      .should('not.be.checked');

    cy.url()
      .should('contain', 'breakdown=false');

    cy.get('#summary label.filter-breakdown input:visible')
      .check();

    cy.reload();

    cy.url()
      .should('contain', 'breakdown=true');
  });

  it('merge all groups: url params should persist after change and reload', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check();

    cy.url()
      .should('contain', 'mergegroup=reposense%2FRepoSense%5Bcypress%5D');

    cy.reload();

    cy.url()
      .should('contain', 'mergegroup=reposense%2FRepoSense%5Bcypress%5D');
  });

  it('show tags: url params should persist after change and reload', () => {
    cy.get('#summary label.show-tags input:visible')
      .should('be.visible')
      .check();

    cy.url()
      .should('contain', 'viewRepoTags=true');

    cy.reload();

    cy.url()
      .should('contain', 'viewRepoTags=true');
  });

  it('optimise timeline: url params should persist after change and reload', () => {
    cy.get('#summary label.optimise-timeline input:visible')
      .should('be.visible')
      .check();

    cy.url()
      .should('contain', 'optimiseTimeline=true');

    cy.reload();

    cy.url()
      .should('contain', 'optimiseTimeline=true');
  });

  it('checked file types: url params should persist after change and reload', () => {
    cy.get('#summary label.filter-breakdown input:visible')
      .should('not.be.checked');

    // Assumption: gradle is the first file type and yml is the last file type to appear in the list
    cy.url()
      .should('not.contain', 'gradle');

    cy.url()
      .should('not.contain', 'yml');

    cy.get('#summary label.filter-breakdown input:visible')
      .check()
      .should('be.checked');

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .should('be.checked');

    cy.get('#summary div.fileTypes input[id="yml"]')
      .should('be.checked');

    cy.url()
      .should('contain', 'gradle');

    cy.url()
      .should('contain', 'yml');

    cy.reload();

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .should('be.checked');

    cy.get('#summary div.fileTypes input[id="yml"]')
      .should('be.checked');

    cy.url()
      .should('contain', 'gradle');

    cy.url()
      .should('contain', 'yml');

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .uncheck()
      .should('not.be.checked');

    cy.url()
      .should('not.contain', 'gradle');

    cy.url()
      .should('contain', 'yml');

    cy.reload();

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .should('not.be.checked');

    cy.get('#summary div.fileTypes input[id="yml"]')
      .should('be.checked');

    cy.url()
      .should('not.contain', 'gradle');

    cy.url()
      .should('contain', 'yml');
  });

  it('code panel: sort by: url params should persist after change and reload', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('div.mui-select.sort-by > select:visible')
      .invoke('val')
      .should('eq', 'linesOfCode');

    cy.url()
      .should('not.contain', 'authorshipSortBy');

    /* Select file name and test URL before and after reload */
    cy.get('div.mui-select.sort-by > select:visible')
      .select('fileName');

    cy.url()
      .should('contain', 'authorshipSortBy=fileName');

    cy.reload();

    cy.url()
      .should('not.contain', '%23%2F');

    cy.url()
      .should('contain', 'authorshipSortBy=fileName');

    /* Select file type and test URL before and after reload */
    cy.get('div.mui-select.sort-by > select:visible')
      .select('fileType');

    cy.url()
      .should('contain', 'authorshipSortBy=fileType');

    cy.reload();

    cy.url()
      .should('not.contain', '%23%2F');

    cy.url()
      .should('contain', 'authorshipSortBy=fileType');
  });

  it('code panel: order: url params should persist after change and reload', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('div.mui-select.sort-order > select:visible')
      .invoke('val')
      .should('eq', 'true'); // true is Descending

    cy.url()
      .should('not.contain', 'reverseAuthorshipOrder');

    /* Select ascending and test URL before and after reload */
    cy.get('div.mui-select.sort-order > select:visible')
      .select('false');

    cy.url()
      .should('contain', 'reverseAuthorshipOrder=false');

    cy.reload();

    cy.url()
      .should('not.contain', '%23%2F');

    cy.url()
      .should('contain', 'reverseAuthorshipOrder=false');

    /* Select descending and test URL before and after reload */

    cy.get('div.mui-select.sort-order > select:visible')
      .select('true');

    cy.url()
      .should('contain', 'reverseAuthorshipOrder=true');

    cy.reload();

    cy.url()
      .should('not.contain', '%23%2F');

    cy.url()
      .should('contain', 'reverseAuthorshipOrder=true');
  });

  it('code panel: filter by glob: url params should persist after change and reload', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    // click on filter glob radio button
    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // enter some input
    cy.get('#search')
      .type('README.md');

    // submit
    cy.get('#search')
      .type('{enter}');

    cy.url()
      .should('contain', 'authorshipFilesGlob=README.md');

    // Some bugs appear after two reloads, so reload twice here
    cy.reload();
    cy.reload();

    cy.url()
      .should('not.contain', '%23%2F');

    cy.url()
      .should('contain', 'authorshipFilesGlob=README.md');
  });
});
