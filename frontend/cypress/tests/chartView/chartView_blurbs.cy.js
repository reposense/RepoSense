describe('blurbs', () => {
  it('shows blurbs', () => {
    cy.get('.markdown.blurb')
      .first()
      .should('contain', 'first blurb');

    cy.get('.markdown.blurb')
      .eq(1)
      .should('contain', 'second blurb');

    cy.get('.markdown.blurb')
      .eq(2)
      .should('contain', 'third blurb');
  });

  it('has the correct number of valid blurbs', () => {
    cy.get('.markdown.blurb')
      .should('have.length', 3);
  });
});
