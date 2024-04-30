describe('scroll to active repo', () => {
  // need to set scrollBehavior to false because the default behavior is to scroll the element into view
  it('selecting a visible repo should not scroll', { scrollBehavior: false }, () => {
    // close the error message box
    cy.get('.error-message-box')
      .should('be.visible');

    cy.get('#summary-wrapper > #summary > .error-message-box > .error-message-box__close-button')
      .click();

    cy.get('.error-message-box')
      .should('not.be.visible');

    cy.get('.icon-button.fa-code')
      .should('exist')
      .first();

    let scrollTopOriginal = 0;
    cy.get('#summary-wrapper')
      .first()
      .then(($el) => {
        scrollTopOriginal = $el.prop('scrollTop');
      });

    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#summary-wrapper')
      .first()
      .then(($el) => {
        const scrollTop = $el.prop('scrollTop');
        expect(scrollTop).to.equal(scrollTopOriginal);
      });
  });

  it('selecting a non-visible repo should scroll', () => {
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
      .should('contain', 'tabRepo=reposense%2Fpublish-RepoSense%5Bmaster%5D');

    cy.reload();

    cy.get('.icon-button.fa-code')
      .should('exist')
      .last()
      .should('be.visible');
  });
});
