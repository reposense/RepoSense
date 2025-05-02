describe('contribution bar',
  { baseUrl: Cypress.env('portfolioBaseUrl') },
  () => {
    it('should display global since and until dates in tooltip', () => {
      const expectedDates = '2019-01-20T00:00:00 to 2023-02-02T23:59:59';

      // Without filter breakdown enabled
      cy.get('.filter-breakdown > input')
        .uncheck()

      cy.get('.summary-chart__contrib')
        .each(($el) => {
          const title = $el.attr('title');
          expect(title).to.include(expectedDates);
        });

      // With filter breakdown enabled
      cy.get('.filter-breakdown > input')
        .check()

      cy.get('.stacked-bar__contrib--bar')
        .each(($el) => {
          const title = $el.attr('title');
          expect(title).to.include(expectedDates);
        });
    });
  },
);
