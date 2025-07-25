describe('author blurbs', () => {
  it('test display of author blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author')
    cy.get('.markdown.blurb')
      .first()
      .should('contain', 'first blurb');

    cy.get('.markdown.blurb')
        .eq(1)
        .should('contain', 'first chart blurb');

    cy.get('.markdown.blurb')
        .eq(2)
        .should('contain', 'second blurb');

    cy.get('.markdown.blurb')
        .eq(3)
        .should('contain', 'third blurb');

    cy.get('.markdown.blurb')
        .eq(4)
        .should('contain', 'third chart blurb');

    cy.get('.markdown.blurb')
        .eq(5)
        .should('contain', 'second chart blurb');

  });

  it('test correct number of blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .should('have.length', 6);
  })

  it('processes html in blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .eq(2)
      .find('h1')
      .contains('dummy text for the second blurb');

    cy.get('.markdown.blurb')
        .eq(5)
        .find('h2')
        .contains('second chart blurb');
  })

  it('processes markdown in blurbs', () => {
    cy.get('#summary .mui-select.grouping select').select('Author');
    cy.get('.markdown.blurb')
      .eq(3)
      .find('h2')
      .contains('h2 tag for the third blurb');

    cy.get('.markdown.blurb')
        .eq(3)
        .find('h3')
        .contains('h3 third chart blurb');
  })
})
