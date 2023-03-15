describe('include merge commits in zoom view', () => {
  it('show merge commits when all file types selected', () => {
    // open the commits panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // check if the icon for merge commits is visible
    cy.get('.code-merge-icon')
      .should('exist');
  });

  it('hide merge commits when some file types unselected', () => {
    // open the commits panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // uncheck the java file type
    cy.get('#tab-zoom .fileTypes input[value="java"]')
      .uncheck()
      .should('not.be.checked');

    // check if the icon for merge commits is not visible
    cy.get('.code-merge-icon')
      .should('not.exist');
  });
});
