describe('code view', function () {
  it('collapsable', function () {
    cy.visit('/');

    cy.get('#tabs-wrapper')
      .should('be.visible');

    cy.get('div.tab-close')
      .click();

    cy.get('#tabs-wrapper')
      .should('not.exist');
  });
});
