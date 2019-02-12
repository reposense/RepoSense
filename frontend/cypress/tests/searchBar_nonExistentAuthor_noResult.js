describe('search non-existent author', function () {
  it('shows no result', function () {
    cy.visit('/');

    cy.get('#summary-wrapper input[type=text]')
      .type('abcdef')
      .type('{enter}');

    // Enter does not work, let's manually submit form
    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
        // $ele is a JQuery object
        const content = $ele.html();

        // debugger // Can insert breakpoint like this and debug using Chrome DevTools

        expect(content).to.be.empty;
    });
  });
});
