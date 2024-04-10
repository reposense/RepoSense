describe('merge group', () => {
  it('check and uncheck merge group when group by repos', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // after checking merge group, only four merged repo groups will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 4);

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    // after un-checking merge group, all 11 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 11);
  });

  it('check and uncheck merge group when group by authors', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // after checking merge group, 11 merged author groups will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 11);

    cy.get('#summary label.merge-group > input:visible')
      .first()
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    // after un-checking merge group, all 11 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 11);
  });

  it('merge group option should be disabled when group by none', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByNone');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .should('be.disabled');
  });

  it('should have the correct number of merge group contribution bars and correct length', () => {
    // Assumption: The number of merge group contribution bars is 3 and the width of the third bar is 50%.
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // get the three chart bars and assert they have the correct initial widths
    cy.get('.stacked-bar__contrib--bar')
      .should('have.length', 6)
      .then(($bars) => {
        // calculate the percentage of the width relative to the parent container
        const parentWidth = $bars.eq(0).parent().width();
        const width1 = (parseFloat(window.getComputedStyle($bars[0]).width) / parentWidth) * 100;
        const width2 = (parseFloat(window.getComputedStyle($bars[1]).width) / parentWidth) * 100;
        const width3 = (parseFloat(window.getComputedStyle($bars[2]).width) / parentWidth) * 100;
        const width4 = (parseFloat(window.getComputedStyle($bars[3]).width) / parentWidth) * 100;
        const width5 = (parseFloat(window.getComputedStyle($bars[4]).width) / parentWidth) * 100;
        const width6 = (parseFloat(window.getComputedStyle($bars[5]).width) / parentWidth) * 100;

        // assert that the widths are close enough to 100% and 50%
        expect(width1).to.be.closeTo(100, 1);
        expect(width2).to.be.closeTo(100, 1);
        expect(width3).to.be.closeTo(100, 1);
        expect(width4).to.be.closeTo(100, 1);
        expect(width5).to.be.closeTo(75, 5);
        expect(width6).to.be.closeTo(25, 5);
      });
  });

  it('merge group contribution bars should have correct width after reload', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    const initialWidths = [];

    // Store the initial widths of the contribution bars
    cy.get('.stacked-bar__contrib--bar')
      .each(($bar) => {
        const width = window.getComputedStyle($bar[0]).width;
        initialWidths.push(width);
      })
      .then(() => {
        // Reload the page and wait for the loading div to disappear
        cy.reload();
        cy.get('.overlay-loader').should('not.be.visible');

        // Get the contribution bars again and compare their widths with the initial widths
        cy.get('.stacked-bar__contrib--bar')
          .should('have.length', initialWidths.length)
          .each(($bar, index) => {
            const width = window.getComputedStyle($bar[0]).width;
            expect(width).to.equal(initialWidths[index]);
          });
      });
  });
});
