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

  it('show ramp chart for selected commits when data range changed', () => {
    // change since date
    cy.get('input[name="since"]:visible')
      .type('2018-06-10');

    // chnage until date
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
});
