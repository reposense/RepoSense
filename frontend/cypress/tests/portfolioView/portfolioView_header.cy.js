describe('portfolio view',
  { baseUrl: Cypress.env('portfolioBaseUrl') },
  () => {
    it('should only display filter breakdown and optimise timeline checkboxes', () => {
      cy.get('#summary > .summary-picker > .summary-picker__section')
        .children()
        .should('have.length', 1);

      cy.get('#summary .summary-picker__section.summary-picker__checkboxes')
        .children()
        .should('have.length', 2);

      cy.get('#summary label.filter-breakdown')
        .should('exist');

      cy.get('#summary label.optimise-timeline')
        .should('exist');
    });

    it('should automatically check filter breakdown and optimise timeline', () => {
      cy.get('#summary label.filter-breakdown > input')
        .should('be.checked');

      cy.get('#summary label.optimise-timeline > input')
        .should('be.checked');
    });

    it('should not display index and name', () => {
      cy.get('#summary .summary-chart__title')
        .should('exist');

      cy.get('#summary .summary-chart__title--index')
        .should('not.exist');

      cy.get('#summary .summary-chart__title--name')
        .should('not.exist');
    });
  }
);
