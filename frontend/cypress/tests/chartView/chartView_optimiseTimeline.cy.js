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

  // it('start and end date indicators should exist', () => {
  //   cy.get('#summary label.optimise-timeline > input:visible')
  //     .should('be.visible')
  //     .check()
  //     .should('be.checked');

  //   cy.get('#summary-charts .summary-chart')
  //     .first()
  //     .find('.summary-chart__ramp .date-indicators span')
  //     .first()
  //     .should('have.text', '2018-05-03');

  //   cy.get('#summary-charts .summary-chart')
  //     .first()
  //     .find('.summary-chart__ramp .date-indicators span')
  //     .last()

  //     // 3/3 on GitHub CI, 3/4 on local
  //     .should('have.text', '2023-03-03');
  // });

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
  
  it('zoom panel range should work correctly when timeline is optimised', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');
      
    cy.get('#tab-zoom .ramp a')
      .first()
      .invoke('css', 'right')
      .then((val) => parseFloat(val))
      .should('gt', 0);

    cy.get('#summary label.optimise-timeline > input')
      .check()
      .should('be.checked');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .period')
      .should('contain', '2018-05-03 to 2023-03-04');

    cy.get('#tab-zoom .ramp a')
      .first()
      .invoke('css', 'right')
      .then((val) => parseFloat(val))
      .should('lt', 1);
  });

  it('subzoom panel range should work correctly when timeline is optimised', () => {
    const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';

    cy.get('#summary label.optimise-timeline > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(110, 20)
      .click(120, 20);

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
