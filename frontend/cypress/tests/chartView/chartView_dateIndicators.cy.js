describe('date indicators', () => {
  it('should show tighter date range from summary.json', () => {
    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2019-01-31')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2025-01-01')

    // change since date
    cy.get('input[name="since"]')
      .type('2019-01-01');

    // change until date
    cy.get('input[name="until"]')
      .type('2025-01-31');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2019-01-31')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2025-01-01')
  });

  it('should show tighter date range from filtered date range', () => {
    // change since date
    cy.get('input[name="since"]')
      .type('2019-12-31');

    // change until date
    cy.get('input[name="until"]')
      .type('2020-01-01');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2019-12-31')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2020-01-01')
  });
});
