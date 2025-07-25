describe('blurbs', () => {
  it('shows blurbs', () => {
    cy.get('.markdown.blurb')
      .first()
      .should('contain', 'first blurb');

    cy.get('.markdown.blurb')
      .eq(1)
      .should('contain', 'third chart blurb');

    cy.get('.markdown.blurb')
        .eq(2)
        .should('contain', 'second blurb');

    cy.get('.markdown.blurb')
      .eq(3)
      .should('contain', 'second chart blurb');

    cy.get('.markdown.blurb')
        .eq(4)
        .should('contain', 'third blurb');

    cy.get('.markdown.blurb')
        .eq(5)
        .should('contain', 'first chart blurb');

  });

  it('has the correct number of valid blurbs', () => {
    cy.get('.markdown.blurb')
      .should('have.length', 6);
  });

  it('processes markdown in blurbs', () => {
    cy.get('.markdown.blurb')
      .eq(2)
      .find('h1')
      .contains('second blurb in h1 tag');

    cy.get('.markdown.blurb')
        .eq(1)
        .find('h3')
        .contains('h3 third chart blurb');
  });

  it('processes html in blurbs', () => {
    cy.get('.markdown.blurb')
      .eq(4)
      .find('h2')
      .contains('third blurb in h2 markdown tag');

    cy.get('.markdown.blurb')
        .eq(3)
        .find('h2')
        .contains('second chart blurb');
  });
});
