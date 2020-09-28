describe('reload page', () => {
  it('reload page should restore all controls', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .select('Time');

    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .select('Descending');

    cy.reload();

    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .should('have.value', 'time');

    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .should('have.value', 'true');
  });
});
