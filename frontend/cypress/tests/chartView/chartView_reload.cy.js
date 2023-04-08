describe('reload page', () => {
  it('reload page should restore all controls', () => {
    cy.wait(100);
    // search
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugene')
      .type('{enter}')
      .wait(100);

    // group by
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors').wait(100);

    // sort groups by
    cy.get('div.mui-select.sort-group > select:visible')
      .select('↓ contribution').wait(100);

    // sort within groups by
    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('↓ contribution').wait(100);

    // granularity
    cy.get('div.mui-select.granularity > select:visible')
      .select('week').wait(100);

    // since date
    cy.get('input[name="since"]:visible')
      .invoke('val', '2018-06-10');
    cy.get('input[name="since"]:visible')
      .trigger('input').wait(100);

    // until date
    cy.get('input[name="until"]:visible')
      .invoke('val', '2019-06-10');
    cy.get('input[name="until"]:visible')
      .trigger('input').wait(100);

    // break down by file type
    cy.get('#summary label.filter-breakdown > input:visible')
      .should('be.visible')
      .check().wait(100);

    cy.get('#summary label.filter-breakdown > input:visible')
      .should('be.checked');

    // merge group
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check();
    cy.get('#summary label.merge-group > input:visible')
      .should('be.checked');

    cy.reload();

    cy.get('div.mui-textfield.search_box > input:visible')
      .should('have.value', 'eugene');

    cy.get('div.mui-select.grouping > select:visible')
      .should('have.value', 'groupByAuthors');

    cy.get('div.mui-select.sort-group > select:visible')
      .should('have.value', 'totalCommits-dsc');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .should('have.value', 'totalCommits-dsc');

    cy.get('div.mui-select.granularity > select:visible')
      .should('have.value', 'week');

    cy.get('input[name="since"]:visible')
      .should('have.value', '2018-06-10');

    cy.get('input[name="until"]:visible')
      .should('have.value', '2019-06-10');

    cy.get('#summary label.filter-breakdown > input:visible')
      .should('be.checked');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.checked');
  });
});
