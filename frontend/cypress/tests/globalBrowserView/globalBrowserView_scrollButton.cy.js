describe('scroll button in global browser view', () => {
  beforeEach(() => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');
  });

  it('button is not shown when only scroll 100 pixels', () => {
    cy.get('#tabs-wrapper').scrollTo(0, 100);
    cy.get('#tabs-wrapper .go-back-button').should('not.be.visible');
    cy.get('#tabs-wrapper').scrollTo(0, 0);
  });

  it('shows the button when scroll more than 200 pixels', () => {
    cy.get('#tabs-wrapper').scrollTo(0, 300).trigger('scroll');
    cy.get('#tabs-wrapper .go-back-button').should('be.visible');
    cy.get('#tabs-wrapper').scrollTo(0, 0);
  });

  it('button scrolls back to top when clicked', () => {
    cy.get('#tabs-wrapper').scrollTo(0, 300).trigger('scroll');
    cy.get('#tabs-wrapper .go-back-button').should('be.visible');
    cy.get('#tabs-wrapper .go-back-button').click();
    cy.get('#tabs-wrapper').invoke('scrollTop').should('equal', 0);
  });
});
