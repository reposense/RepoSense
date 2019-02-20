describe('search non-existent author', () => {
  it('shows no result', () => {
    cy.visit('/');

    cy.get('#summary-wrapper input[type=text]')
        .type('abcdef')
        .type('{enter}');

    // Enter does not work. Related issue: https://github.com/cypress-io/cypress/issues/3405
    // Let's manually submit form
    cy.get('#summary-wrapper form.summary-picker')
        .submit();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const content = $ele.html();

      expect(content).to.be.empty;
    });
  });
});
