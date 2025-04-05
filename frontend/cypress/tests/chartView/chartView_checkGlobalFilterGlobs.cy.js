Cypress.on('uncaught:exception', (err) => {
  if (err.message.includes('_ctx.onTooltipHover is not a function')) {
    return false;
  }
  return true;
});

describe('Global filter globs display test', () => {
  it('the global globs search bar should exists', () => {
    cy.get('input[type="search"]').should('be.visible');
  })

  it('the input to filter globs should be displayed', () => {
    cy.get('input[type="search"]').type('filterKeyword{enter}');
    cy.get('input[type="search"]').should('have.value', 'filterKeyword');

  })

  it('the global globs search should be able to clear the input', () => {
    cy.get('input[type="search"]').type('example');
    cy.get('button.mui-btn--raised').eq(0).click();
    cy.get('input[type="search"]').should('have.value', '');
  })
})
