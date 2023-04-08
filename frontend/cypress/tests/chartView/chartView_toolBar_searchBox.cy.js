describe('search bar', () => {
  it('non-existent author shows no result', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .type('abcdef')
      .type('{enter}');

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const content = $ele.html();

      expect(content).to.be.empty;
    });
  });

  it('unique author shows one result', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .type('Yong Hao TENG')
      .type('{enter}');

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const children = $ele.children().length;
      expect(children).to.equal(1);
    });
  });
});
