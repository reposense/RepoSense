describe('error summary', () => {
  it('shows list of issues encountered while analyzing repos', () => {
    cy.get('.error-message-box')
      .should('be.visible');

    cy.get('#summary-wrapper > #summary > .error-message-box > .error-message-box__message').then(($ele) => {
      const expected = 'The following issues occurred when analyzing the following repositories:';
      const message = $ele.text();

      expect(expected).to.equal(message);
    });

    cy.get('.error-message-box__failed-repo--name')
      .should('be.visible');

    cy.get('.error-message-box__failed-repo--reason')
      .should('be.visible');
  });

  it('can be closed', () => {
    cy.get('.error-message-box')
      .should('be.visible');

    cy.get('#summary-wrapper > #summary > .error-message-box > .error-message-box__close-button')
      .click();

    cy.get('.error-message-box')
      .should('not.be.visible');
  });

  it('number of error messages', () => {
    cy.get('.error-message-box')
      .find('.error-message-box__failed-repo')
      .should('have.length', 4);

    cy.get('.error-message-box__show-more-container > a')
      .click();

    cy.get('.error-message-box')
      .find('.error-message-box__failed-repo')
      .should('have.length', 5);

    cy.get('.error-message-box__show-more-container > a')
      .click();

    cy.get('.error-message-box')
      .find('.error-message-box__failed-repo')
      .should('have.length', 4);
  });
});
