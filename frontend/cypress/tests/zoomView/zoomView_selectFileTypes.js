describe('check file types ', () => {
  it('check if all file types are visible by default', () => {
    Cypress.wait();

    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .zoom__day', { timeout: 90000 })
        .should('exist');

    cy.get('#tab-zoom .fileTypes input[value="all"]')
        .should('be.checked');
  });

  it('uncheck all file types should show no files', () => {
    Cypress.wait();

    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .zoom__day')
        .should('exist');

    cy.get('#tab-zoom .fileTypes input[value="all"]')
        .uncheck();

    cy.get('#tab-zoom .zoom__day')
        .should('not.exist');
  });

  it('uncheck file type should uncheck all option and not show legend', () => {
    // Assumptions: the first author of the first repo
    // committed .java, .js and .gradle files.
    Cypress.wait();

    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .fileTypes input[value="java"]')
        .uncheck()
        .should('not.be.checked');

    cy.get('#tab-zoom .fileTypes input[value="js"]')
        .uncheck()
        .should('not.be.checked');

    cy.get('.zoom__day > .commit-message > .fileTypeLabel')
        .should('not.contain.text', 'java');

    cy.get('.zoom__day > .commit-message > .fileTypeLabel')
        .should('contain.text', 'gradle');
  });
});
