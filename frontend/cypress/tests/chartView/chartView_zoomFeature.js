describe('zoom features in code view', () => {
  const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';
  it('click on view commits button', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');
  });

  it('zoom into ramp', () => {
    // clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(10, 20)
      .click(50, 20);

    cy.get('#tab-zoom')
      .should('be.visible');
  });

  it('zoom into ramp when merge group', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(10, 20)
      .click(50, 20);

    cy.get('#tab-zoom')
      .should('be.visible');
  });
});
