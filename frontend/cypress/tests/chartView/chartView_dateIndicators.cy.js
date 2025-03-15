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

    // change since date to day before repository since date
    cy.get('input[name="since"]')
      .type('2019-01-30');

    // change until date to day after repository until date
    cy.get('input[name="until"]')
      .type('2025-01-02');

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
    // change since date to day after repository since date
    cy.get('input[name="since"]')
      .type('2019-02-01');

    // change until date to day before repository until date
    cy.get('input[name="until"]')
      .type('2024-12-31');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2019-02-01')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2024-12-31')
  });

  it('should show tighter date range when until date is out of repository date range', () => {
    // change since date to repository until date
    cy.get('input[name="since"]')
      .type('2025-01-01');

    // change until date to day after repository until date
    cy.get('input[name="until"]')
      .type('2025-01-02');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2025-01-01')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2025-01-01')
  });

  it('should show tighter date range when since date is out of repository date range', () => {
    // change since date to day before repository since date
    cy.get('input[name="since"]')
      .type('2019-01-30');

    // change until date to repository since date
    cy.get('input[name="until"]')
      .type('2019-01-31');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '2019-01-31')

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '2019-01-31')
  });

  it('should show out of range warning when filter dates are before repository date range', () => {
    // change since date
    cy.get('input[name="since"]')
      .type('2018-12-31');

    // change until date to day before repository since date
    cy.get('input[name="until"]')
      .type('2019-01-30');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators .warn')
      .should('exist')
      .should('have.text', 'Filter dates out of repository\'s date range.')
  });

  it('should show out of range warning when filter dates are after repository date range', () => {
    // change since date to day after repository until date
    cy.get('input[name="since"]')
      .type('2025-01-02');

    // change until date
    cy.get('input[name="until"]')
      .type('2025-01-30');

    cy.get('#summary-charts .summary-chart')
      .last()
      .find('.summary-chart__ramp .date-indicators .warn')
      .should('exist')
      .should('have.text', 'Filter dates out of repository\'s date range.')
  });
});
