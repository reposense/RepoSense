describe('author blurbs', () => {
  it('test display of author blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author')
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

  it('test correct number of blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .should('have.length', 3);
  })

  it('processes html in blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .eq(1)
      .find('h1')
      .contains('dummy text for the second blurb');
  })

  it('processes markdown in blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .eq(2)
      .find('h2')
      .contains('h2 tag for the third blurb');
  })
})
