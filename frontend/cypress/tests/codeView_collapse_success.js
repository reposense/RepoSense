describe('code view', () => {
  it('collapsable', () => {
    cy.visit('/');

    cy.get('#tabs-wrapper')
      .should('be.visible');

    cy.get('div.tab-close')
      .click();

    cy.get('#tabs-wrapper')
      .should('not.exist');
  });
});
