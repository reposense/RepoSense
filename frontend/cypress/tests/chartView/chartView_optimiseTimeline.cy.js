describe('optimise timeline', () => {
  it('ramp padding should only exist when optimise timeline is checked', () => {
    cy.get('#summary label.optimise-timeline > input:visible')
      .should('be.visible')
      .uncheck()
      .should('be.not.checked');

    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .ramp .ramp-padding')
      .should('have.css', 'left', '0px');

    cy.get('#summary label.optimise-timeline > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .ramp .ramp-padding')
      .should('have.not.css', 'left', '0px');
  });

  it('should retain the same number of ramp slices', () => {
    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .ramp .ramp-padding a')
      .then(($el) => {
        const rampSlices = $el.length;

        cy.get('#summary label.optimise-timeline > input:visible')
          .should('be.visible')
          .check()
          .should('be.checked');

        cy.get('#summary-charts .summary-chart')
          .first()
          .find('.summary-chart__ramp .ramp .ramp-padding a')
          .should('have.length', rampSlices);
      });
  });

  it('start and end date indicators should exist', () => {
    cy.get('#summary label.optimise-timeline > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .date-indicators span')
      .first()
      .should('have.text', '5/3/2018');

    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .date-indicators span')
      .last()
      .should('have.text', '3/4/2023');
  });

  it('no commits in range should not have date indicators', () => {
    cy.get('#summary label.optimise-timeline > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // change since date
    cy.get('input[name="since"]')
      .type('2018-12-31');

    // change until date
    cy.get('input[name="until"]')
      .type('2019-01-01');

    cy.get('#summary-charts .summary-chart')
      .first()
      .find('.summary-chart__ramp .date-indicators')
      .should('not.exist');
  });
});
