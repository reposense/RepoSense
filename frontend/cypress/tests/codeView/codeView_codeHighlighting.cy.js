describe('code highlighting works properly', () => {
  it('line numbers should all have the same colour', () => {
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('.line-number')
      .first()
      .should('have.css', 'color')
      .then((firstColor) => {
        cy.get('.line-number')
          .each((el) => {
            expect(el.css('color')).to.equal(firstColor);
          },
        )
      }
    );
  });
});
