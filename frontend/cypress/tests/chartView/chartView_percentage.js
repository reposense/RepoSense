describe('sort by contribution', () => {
  it('percentage should only be displayed when sort by contribution', () => {
    Cypress.wait();

    cy.get('#summary .summary-chart__title--percentile')
        .should('not.exist');

    cy.get('div.mui-select.sort-group > select:visible')
        .select('↑ contribution');

    cy.get('#summary .summary-chart__title--percentile')
        .should('exist');

    cy.get('div.mui-select.sort-group > select:visible')
        .select('↓ group title');

    cy.get('#summary .summary-chart__title--percentile')
        .should('not.exist');

    cy.get('div.mui-select.sort-group > select:visible')
        .select('↓ contribution');

    cy.get('#summary .summary-chart__title--percentile')
        .should('exist');

    cy.get('div.mui-select.sort-group > select:visible')
        .select('↑ variance');

    cy.get('#summary .summary-chart__title--percentile')
        .should('not.exist');

    cy.get('div.mui-select.sort-group > select:visible')
        .select('↓ variance');

    cy.get('#summary .summary-chart__title--percentile')
        .should('not.exist');
  });
});
