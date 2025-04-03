describe('optimise timeline',
  { baseUrl: Cypress.env('portfolioBaseUrl') },
  () => {
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
        .uncheck()
        .should('not.be.checked');

      cy.get('#summary-charts .summary-chart')
        .first()
        .find('.summary-chart__ramp .date-indicators span')
        .first()
        .should('have.text', '2019-01-20T00:00:00');

      cy.get('#summary-charts .summary-chart')
        .first()
        .find('.summary-chart__ramp .date-indicators span')
        .last()
        .should('have.text', '2019-12-29T23:59:59');

      cy.get('#summary label.optimise-timeline > input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

      cy.get('#summary-charts .summary-chart')
        .first()
        .find('.summary-chart__ramp .date-indicators span')
        .first()
        .should('have.text', '2019-01-21T00:00:00');

      cy.get('#summary-charts .summary-chart')
        .first()
        .find('.summary-chart__ramp .date-indicators span')
        .last()
        .should('have.text', '2019-06-10T00:00:00');
    });

    it('zoom panel range should work correctly when timeline is not optimised', () => {
      cy.get('#summary label.optimise-timeline > input:visible')
        .should('be.visible')
        .uncheck()
        .should('not.be.checked');

      cy.get('.icon-button.fa-list-ul')
        .should('exist')
        .first()
        .click();

      cy.get('#tab-zoom')
        .should('be.visible');

      // verifies the ramp chart is not optimised and has empty space on the right
      cy.get('#tab-zoom .ramp a')
        .first()
        .invoke('css', 'right')
        .then((val) => parseFloat(val))
        .should('gt', 0);
    });

    it('zoom panel range should work correctly when timeline is optimised', () => {
      cy.get('#summary label.optimise-timeline > input')
        .should('be.visible')
        .check()
        .should('be.checked');

      cy.get('.icon-button.fa-list-ul')
        .should('exist')
        .first()
        .click();

      cy.get('#tab-zoom')
        .should('be.visible');

      // verifies the date range is correctly optimised
      cy.get('#tab-zoom .period')
        .should('contain', '2019-01-21T00:00:00 to 2019-06-10T00:00:00');

      // verifies the ramp chart is optimised and has no empty space on the right
      cy.get('#tab-zoom .ramp a')
        .first()
        .invoke('css', 'right')
        .then((val) => parseFloat(val))
        .should('lt', 1);
    });

    // TODO: Existing bug where commits sometimes do not align with the selected range.
    it.skip('subzoom panel range should work correctly when timeline is optimised', () => {
      const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';

      cy.get('#summary label.optimise-timeline > input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

      // clicking from the 15th px to the 20 px in the ramp
      cy.get('body').type(zoomKey, {release: false})
        .get('#summary-charts .summary-chart__ramp .ramp')
        .first()
        .click(10, 20)
        .click(15, 20);

      cy.get('#tab-zoom')
        .should('be.visible');

      cy.get('#tab-zoom .ramp .ramp__slice')
        .should('have.length', 1);

      cy.get('#tab-zoom .ramp .ramp__slice')
        .invoke('attr', 'title')
        .then((title) => {
          cy.wrap(title).should('eq', '[2019-08-18] AboutUs: update team members (#867): +94 -12 lines ');
        });
    });
  });
