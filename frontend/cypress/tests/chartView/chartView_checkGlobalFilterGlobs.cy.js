describe('Global filter globs display test', () => {
  it('the global globs search bar should exists', () => {
    cy.get('input[type="search"]').should('be.visible');
  })

  it('the input to filter globs should be displayed', () => {
    cy.get('input[type="search"]').type('filterKeyword');
    cy.get('.file-list-item').should('contain', 'filterKeyword');

  })

  it('the global globs search should be able to clear the input', () => {
    cy.get('input[type="search"]').type('example');
    cy.get('button.mui-btn--raised').click();
    cy.get('input[type="search"]').should('have.value', '');
  })
})
