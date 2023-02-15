describe('check file types', () => {
  it('check if all files types are visible by default', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .should('be.checked');
  });

  it('uncheck all files types should show no files', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-authorship .files')
      .should('not.be.visible');
  });

  it('uncheck file type should uncheck all option and not show legend', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="java"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .should('not.be.checked');

    cy.get('.file > .title > .fileTypeLabel')
      .should('not.contain.text', 'java');

    cy.get('.file > .title > .fileTypeLabel')
      .should('contain.text', 'gradle');
  });
});
