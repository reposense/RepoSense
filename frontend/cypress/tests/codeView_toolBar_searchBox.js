describe('search bar', () => {
  it('non-existent author shows no result', () => {
    cy.get('#tabs-wrapper .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
        .type('abcdef')
        .type('{enter}');

    Cypress.wait();

    // Enter does not work. Related issue: https://github.com/cypress-io/cypress/issues/3405
    // Let's manually submit form
    cy.get('#summary-wrapper form.summary-picker')
        .submit();

    Cypress.wait();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const content = $ele.html();

      expect(content).to.be.empty;
    });
  });

  it('unique author shows one result', () => {
    cy.get('#tabs-wrapper .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
        .type('Yong Hao TENG')
        .type('{enter}');

    Cypress.wait();

    // Enter does not work. Related issue: https://github.com/cypress-io/cypress/issues/3405
    // Let's manually submit form
    cy.get('#summary-wrapper form.summary-picker')
        .submit();

    Cypress.wait();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const children = $ele.children().length;
      expect(children).to.equal(1);
    });
  });
});
