describe('render filter hash', () => {
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
      .should('include', 'breakdown=false');

    cy.get('#summary label.filter-breakdown input:visible')
      .check();

    cy.reload();

    cy.url()
      .should('include', 'breakdown=true');
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

  it('checked file types: url params should persist after change and reload', () => {
    cy.get('#summary label.filter-breakdown input:visible')
      .should('not.be.checked');

    cy.url()
      .should('not.contain', 'gradle');

    cy.get('#summary label.filter-breakdown input:visible')
      .check()
      .should('be.checked');

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .should('be.checked');

    cy.url()
      .should('contain', 'gradle');

    cy.reload();

    cy.url()
      .should('contain', 'gradle');

    cy.get('#summary div.fileTypes input[id="gradle"]')
      .uncheck()
      .should('not.be.checked');

    cy.url()
      .should('not.contain', 'gradle');

    cy.reload();

    cy.url()
      .should('not.contain', 'gradle');
  });
});
