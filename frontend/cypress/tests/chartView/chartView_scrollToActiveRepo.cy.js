describe('scroll to active repo', () => {
  // need to set scrollBehavior to false because the default behavior is to scroll the element into view
  it('selecting a visible repo should not scroll', { scrollBehavior: false }, () => {
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#summary-wrapper')
      .first()
      .then(($el) => {
        const scrollTop = $el.prop('scrollTop');
        expect(scrollTop).to.equal(0);
      });
  });

  it('selecting a non-visible repo should scroll', { retries: 8, defaultCommandTimeout: 1000 }, () => {
    cy.get('.icon-button.fa-code')
      .should('exist')
      .last()
      .click();

    cy.get('#summary-wrapper')
      .first()
      .then(($el) => {
        const scrollTop = $el.prop('scrollTop');
        expect(scrollTop).to.not.equal(0);
      });

    cy.url()
      .should('contain', 'tabAuthor=yong24s')
      .should('contain', 'tabRepo=reposense%2FRepoSense%5Bcypress%5D');

    cy.reload();

    cy.wait(1000);

    cy.get('.icon-button.fa-code')
      .should('exist')
      .last()
      .should('be.visible');
  });
});
