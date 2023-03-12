describe('show ramp chart for period', () => {
  // Assumptions: The commit messages on the zoom view are
  // correctly filtered according to the selected period.

  it('show ramp chart for all commits by default', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // ramp chart should have the same number of slices as commits
    cy.get('#tab-zoom .commit-message')
      .its('length').then(($length) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .should('have.length', $length);
      });
  });

  it('show ramp chart for selected commits when date range changed', () => {
    // change since date
    cy.get('input[name="since"]:visible')
      .type('2018-06-10');

    // change until date
    cy.get('input[name="until"]:visible')
      .type('2019-06-10');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // ramp chart should have the same number of slices as commits
    cy.get('#tab-zoom .commit-message')
      .its('length').then(($length) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .should('have.length', $length);
      });
  });

  it('show ramp chart for selected commits when zooming', () => {
    const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';

    // zoom into ramp on summary panel
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(10, 20)
      .click(50, 20);

    // commits panel should be visible
    cy.get('#tab-zoom')
      .should('be.visible');

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // ramp chart should have the same number of slices as commits
    cy.get('#tab-zoom .commit-message')
      .its('length').then(($length) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .should('have.length', $length);
      });
  });

  it('ramps should be between start date and end date', () => {
    // change since date
    cy.get('input[name="since"]:visible')
      .type('2018-06-10');

    // change until date
    cy.get('input[name="until"]:visible')
      .type('2019-06-10');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // first ramp should be for commit after start date
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2018-06-12] Setup AppVeyor CI (#142): +19 -0 lines ');

    // last ramp should be for commit before end date
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2019-03-25] [#622] CsvParser#parse: fix error handling of `processLine` (#623): +30 -10 lines ');
  });

  it('ramp should have expected properties', () => {
    // change until date
    cy.get('input[name="until"]:visible')
      .type('2019-06-10');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // last ramp should have expected z-index
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .should('have.css', 'z-index', '39');

    // last ramp should have expected width
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .should('have.css', 'border-left-width', '7px');
  });

  it('ramps from the different days should have expected relative properties', () => {
    // change until date
    cy.get('input[name="until"]:visible')
      .type('2019-06-10');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // last 2 ramps should have expected relative z-indices
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .then(($el) => $el.css('z-index'))
      .then(parseInt) // get 1st z-index
      .then((index1) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .eq(1)
          .then(($el) => $el.css('z-index'))
          .then(parseInt) // get 2nd z-index
          .then((index2) => {
            expect(index1).to.be.gt(index2);
          });
      });

    // last 2 ramps should have expected relative distances from the right
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .then(($el) => $el.css('right'))
      .then(parseFloat) // get 1st distance
      .then((distance1) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .eq(1)
          .then(($el) => $el.css('right'))
          .then(parseFloat) // get 2nd distance
          .then((distance2) => {
            expect(distance1).to.be.lt(distance2);
          });
      });
  });

  it('ramps from the same day should have expected relative properties', () => {
    // change until date
    cy.get('input[name="until"]:visible')
      .type('2023-03-04');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // last 2 ramps should have expected relative z-indices
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .then(($el) => $el.css('z-index'))
      .then(parseInt) // get 1st z-index
      .then((index1) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .eq(1)
          .then(($el) => $el.css('z-index'))
          .then(parseInt) // get 2nd z-index
          .then((index2) => {
            expect(index1).to.be.eq(index2);
          });
      });

    // last 2 ramps should have expected relative distances from the right
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .then(($el) => $el.css('right'))
      .then(parseFloat) // get 1st distance
      .then((distance1) => {
        cy.get('#tab-zoom .ramp .ramp__slice')
          .eq(1)
          .then(($el) => $el.css('right'))
          .then(parseFloat) // get 2nd distance
          .then((distance2) => {
            expect(distance1).to.be.lt(distance2);
          });
      });
  });

  it('ramp should link to commit webpage', () => {
    // change until date
    cy.get('input[name="until"]:visible')
      .type('2019-06-10');

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // ramp chart should be visible
    cy.get('#tab-zoom > .ramp')
      .should('be.visible');

    // last ramp should have expected link
    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'href')
      .should('eq', 'https://github.com/reposense/RepoSense/commit/19e32944d4c67ed0b6bdc5697016536cc9e17ed9');
  });
});
