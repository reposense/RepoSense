describe('zoom features in code view', () => {
  it('click on view commits button', () => {
    Cypress.wait();

    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom')
        .should('be.visible');
  });

  it('ctrl-clicking zoom range', () => {
    Cypress.wait();

    // ctrl clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type('{meta}', { release: false })
        .get('#summary-charts .summary-chart__ramp .ramp')
        .first()
        .click(10, 20)
        .click(50, 20);

    cy.get('#tab-zoom')
        .should('be.visible');
  });

  it('ctrl-clicking zoom range when merge group', () => {
    Cypress.wait();

    cy.get('#summary label.merge-group > input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

    // ctrl clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type('{meta}', { release: false })
        .get('#summary-charts .summary-chart__ramp .ramp')
        .first()
        .click(10, 20)
        .click(50, 20);

    cy.get('#tab-zoom')
        .should('be.visible');
  });
});
