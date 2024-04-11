describe('blurbs', () => {
  it('shows blurbs', () => {
    cy.get('.markdown.blurb')
      .first()
      .should('contain', 'first blurb');
  });

  it('has the correct number of valid blurbs', () => {
    cy.get('.markdown.blurb')
      .should('have.length', 1);
  });
});
