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

    // after un-checking merge group, all 14 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 14);
  });

  it('check and uncheck merge group when group by authors', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // after checking merge group, 14 merged author groups will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 14);

    cy.get('#summary label.merge-group > input:visible')
      .first()
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    // after un-checking merge group, all 14 summary charts will show
    cy.get('#summary-charts').find('.summary-chart')
      .should('have.length', 14);
  });

  it('merge group option should be disabled when group by none', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByNone');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .should('be.disabled');
  });

  it('should have the correct number of merge group contribution bars and correct length', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // get the chart bars and assert they have the correct initial widths
    const expectedWidths = [100, 100, 100, 15, 100, 100, 90, 30, 15];
    cy.get('.stacked-bar__contrib--bar')
      .should('have.length', expectedWidths.length)
      .then(($bars) => {
        // calculate the percentage of the width relative to the parent container
        const parentWidth = $bars.eq(0).parent().width();
        expectedWidths.forEach((expectedWidth, index) => {
          const width = (parseFloat(window.getComputedStyle($bars[index]).width) / parentWidth) * 100;
          expect(width).to.be.closeTo(expectedWidth, 1);
        });
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
