describe('contribution bars', () => {
  it('should render container for contribution bars', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message .stacked-bar-container')
      .should('be.visible');
  });
});
