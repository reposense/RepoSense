describe('code highlighting works properly', () => {
  it('line numbers should all have the same colour', () => {
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('.line-number')
      .should('have.css', 'color', 'rgb(158, 158, 158)') // #9E9E9E
      .then((color) => {
        const firstColor = color;
        cy.get('.line-number')
          .each(($el) => {
            expect($el.css('color')).to.equal(firstColor);
          });
      });
  })
});