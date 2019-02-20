describe('search unique author', () => {
  it('shows one result', () => {
    cy.visit('/');

    cy.get('#summary-wrapper input[type=text]')
        .type('Yong Hao TENG')
        .type('{enter}');

    // Enter does not work. Related issue: https://github.com/cypress-io/cypress/issues/3405
    // Let's manually submit form
    cy.get('#summary-wrapper form.summary-picker')
        .submit();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const children = $ele.children().length;
      expect(children).to.equal(1);
    });
  });
});
