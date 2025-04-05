describe('sorting', () => {
  it('sort by default should sort by order of rows in repo-config.csv', () => {
    // group by
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // sort groups by
    cy.get('div.mui-select.sort-group > select:visible')
      .select('↑ default');

    // Select the first element with the class 'summary-charts__title--groupname' and verify the text
    cy.get('.summary-charts__title--groupname')
      .first()
      .should('have.text', 'reposense/RepoSense[cypress]');
  });

  it('sort by default dsc should sort by descending order of rows in repo-config.csv', () => {
    // group by
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // sort groups by
    cy.get('div.mui-select.sort-group > select:visible')
      .select('↓ default');

    // Select the first element with the class 'summary-charts__title--groupname' and verify the text
    cy.get('.summary-charts__title--groupname')
      .first()
      .should('have.text', 'reposense/RepoSense-auth-helper[master]');
  });
});
