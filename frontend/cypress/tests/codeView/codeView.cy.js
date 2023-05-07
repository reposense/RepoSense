describe('code view', () => {
  it('at first start shows tutorial message', () => {
    cy.get('#tabs-wrapper')
      .should('be.visible');

    cy.get('#tab-empty > .title').then(($ele) => {
      const expected = 'Welcome to this RepoSense report!'
        + 'The charts on the left show the contribution activities, grouped by repository and author.'
        + "To view the code attributed to a specific author, click the    icon next to that author's name."
        + "To view the breakdown of commits made by a specific author, click the    icon next to that author's name."
        + 'To hide the code view and show only the activity charts, click the    icon on the centre divider.'
        + 'See the  User Guide  to get a better understanding of how to interpret the report.';
      const message = $ele.text();

      expect(expected).to.equal(message);
    });
  });

  it('can be closed', () => {
    cy.get('#tabs-wrapper')
      .should('be.visible');

    cy.get('#app #tab-resize .tab-close')
      .click();

    cy.get('#tabs-wrapper')
      .should('not.exist');
  });

  it('merge group and view code for entire repository', () => {
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check({ force: true })
      .should('be.checked');

    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
  });
});
