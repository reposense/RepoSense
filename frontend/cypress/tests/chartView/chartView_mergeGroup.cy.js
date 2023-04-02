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

  it('merge group contribution bars have correct number and width after reload', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // get the three chart bars and assert they have the correct initial widths
    cy.get('.summary-chart__contrib--bar')
      .should('have.length', 3)
      .then(($bars) => {
        // calculate the percentage of the width relative to the parent container
        const parentWidth = $bars.eq(0).parent().width();
        const width1 = (parseFloat(window.getComputedStyle($bars[0]).width) / parentWidth) * 100;
        const width2 = (parseFloat(window.getComputedStyle($bars[1]).width) / parentWidth) * 100;
        const width3 = (parseFloat(window.getComputedStyle($bars[2]).width) / parentWidth) * 100;

        // assert that the widths are close enough to 100% and 50%
        expect(width1).to.be.closeTo(100, 1);
        expect(width2).to.be.closeTo(100, 1);
        expect(width3).to.be.closeTo(50, 1);
      });

    cy.reload();

    // assert that the chart bars still have the correct widths
    cy.get('.summary-chart__contrib--bar')
      .should('have.length', 3)
      .then(($bars) => {
        // calculate the percentage of the width relative to the parent container
        const parentWidth = $bars.eq(0).parent().width();
        const width1 = (parseFloat(window.getComputedStyle($bars[0]).width) / parentWidth) * 100;
        const width2 = (parseFloat(window.getComputedStyle($bars[1]).width) / parentWidth) * 100;
        const width3 = (parseFloat(window.getComputedStyle($bars[2]).width) / parentWidth) * 100;

        // assert that the widths are close enough to 100% and 50%
        expect(width1).to.be.closeTo(100, 1);
        expect(width2).to.be.closeTo(100, 1);
        expect(width3).to.be.closeTo(50, 1);
      });
  });
});
