describe('hide all commit messages ', () => {
  it('check hide all commit messages hides the commit messages', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message', {timeout: 90000})
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

  it('check message of one file is shown, rest are hidden', () => {
    Cypress.wait();

    //open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // the messages of all the commits should be visible
    cy.get('#tab-zoom .commit-message .body')
      .should('be.visible');

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline')
      .should('be.visible')
      .click();

    // should show 'show all commit messages' only
    cy.get('#tab-zoom .toolbar--multiline a')
      .should('not.contain.text', 'hide all commit messages')
      .should('contain.text', 'show all commit messages');

    // open message of the first commit
    cy.get('#tab-zoom .commit-message--button.fa-ellipsis-h')
      .should('be.visible')
      .first()
      .click();

    // should show both 'show/hide all commit messages'
    cy.get('#tab-zoom .toolbar--multiline a')
      .should('contain.text', 'show all commit messages')
      .should('contain.text', 'hide all commit messages');

    // message of the first commit should be visible
    cy.get('#tab-zoom .commit-message .body ')
      .first()
      .should('be.visible');

    // message of the last commit should be hidden
    cy.get('#tab-zoom .commit-message .body ')
      .last()
      .should('not.be.visible');
  });

  it('check show all commit messages show the commit messages', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message', {timeout: 90000})
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
