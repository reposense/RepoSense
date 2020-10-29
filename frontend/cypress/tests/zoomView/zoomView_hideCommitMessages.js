describe('hide all commit messages ', () => {
  it('check hide all commit messages hides the commit messages', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
        .should('be.visible');

    // the messages of all the commits should be visible
    cy.get('#tab-zoom .commit-message .body')
        .should('be.visible');

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline')
        .should('be.visible')
        .click();

    cy.get('#tab-zoom .commit-message')
        .should('be.visible');

    // the messages of all the commits should be hidden
    cy.get('#tab-zoom .commit-message .body')
        .should('not.be.visible');
  });

  it('check show all commit messages show the commit messages', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
        .should('be.visible');

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline')
        .should('be.visible')
        .click();

    // the messages of all the commits should be hidden
    cy.get('#tab-zoom .commit-message .body')
        .should('not.be.visible');

    // show the messages of all the commits
    cy.get('#tab-zoom .toolbar--multiline')
        .should('be.visible')
        .click();

    cy.get('#tab-zoom .commit-message .body')
        .should('be.visible');
  });
});
