describe('authorship tab',
  { baseUrl: Cypress.env('portfolioBaseUrl') },
  () => {
    it('should display global since and until dates', () => {
      const expectedDates = '2019-01-20T00:00:00 to 2023-02-02T23:59:59';

      cy.get('.icon-button.fa-code').each(($el) => {
        cy.wrap($el).click();

        cy.get('#tab-authorship .period > span')
          .last()
          .should('contain.text', expectedDates);
      });
    });
  },
);
