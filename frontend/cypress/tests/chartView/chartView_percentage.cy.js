describe('sort by contribution', () => {
  it('percentage should only be displayed when sort by contribution', () => {
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

  it('percentage is NOT shown even after changing other fields', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('None');

    cy.get('#summary .summary-chart__title--percentile')
      .should('not.exist');

    cy.get('div.mui-select.grouping > select:visible')
      .select('Author');

    cy.get('#summary .summary-chart__title--percentile')
      .should('not.exist');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('↓ contribution');

    cy.get('#summary .summary-chart__title--percentile')
      .should('not.exist');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('↑ variance');

    cy.get('#summary .summary-chart__title--percentile')
      .should('not.exist');
  });

  it('percentage is NOT hidden even after changing other fields', () => {
    cy.get('div.mui-select.sort-group > select:visible')
      .select('↓ contribution');

    cy.get('#summary .summary-chart__title--percentile')
      .should('exist');

    cy.get('div.mui-select.grouping > select:visible')
      .select('None');

    cy.get('#summary .summary-chart__title--percentile')
      .should('exist');

    cy.get('div.mui-select.grouping > select:visible')
      .select('Author');

    cy.get('#summary .summary-chart__title--percentile')
      .should('exist');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('↑ contribution');

    cy.get('#summary .summary-chart__title--percentile')
      .should('exist');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('↓ title');

    cy.get('#summary .summary-chart__title--percentile')
      .should('exist');
  });
});
