describe('merge group', () => {
  it('check and uncheck merge group when group by repos', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // after checking merge group, only one merged repo group will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 1);

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    // after un-checking merge group, all 5 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 5);
  });

  it('check and uncheck merge group when group by authors', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // after checking merge group, 5 merged author groups will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 5);

    cy.get('#summary label.merge-group > input:visible')
      .first()
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    // after un-checking merge group, all 5 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 5);
  });

  it('merge group option should be disabled when group by none', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByNone');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .should('be.disabled');
  });
});
