describe('code view', () => {
  it('at first start shows tutorial message', () => {
    cy.get('#tabs-wrapper')
        .should('be.visible');

    cy.get('#tab-empty > .title').then(($ele) => {
      const expected = 'To view the code attributed to a specific author, click the    icon next to that author\'s name.To hide the code view, click the    icon on the left.';
      const message = $ele.text();

      expect(expected).to.equal(message);
    });
  });

  it('can be closed', () => {
    cy.get('#tabs-wrapper')
        .should('be.visible');

    Cypress.wait();

    cy.get('div.tab-close')
        .click();

    Cypress.wait();

    cy.get('#tabs-wrapper')
        .should('not.exist');
  });
});
