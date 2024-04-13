describe('code highlighting works properly', () => {
  it('line numbers should all have the same colour', () => {
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('.line-number') // this is just a wrapper
      .first()
      .children()
      // the actual line number element
      .first()
      .should('have.css', 'color')
      .then((firstColor) => {
        cy.get('.line-number')
          .each((el) => cy.wrap(el)
            .children()
            .first()
            .should('have.css', 'color', firstColor));
      });
  });
});
