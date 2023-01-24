describe('hide all commit messages ', () => {
  it('check hide all commit messages hides the commit messages', () => {
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

  it('check correct display of show all and hide all commit messages', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
        .should('be.visible');

    // should only display 'hide all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
        .children()
        .should('have.length', 1);

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline > a')
        .should('have.text', 'hide all commit messages')
        .click();

    // should only display 'show all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
        .children()
        .should('have.length', 1);

    cy.get('#tab-zoom .toolbar--multiline > a')
        .should('have.text', 'show all commit messages');

    // first commit message body should be hidden
    cy.get('#tab-zoom .commit-message .body')
        .first()
        .should('not.be.visible');

    // show the message body of the first commit
    cy.get('#tab-zoom .commit-message > a .tooltip')
        .should('be.visible')
        .first()
        .click();

    // first commit message body should be visible
    cy.get('#tab-zoom .commit-message .body')
        .first()
        .should('be.visible');

    // should now display both 'hide all & show all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
        .children()
        .should('have.length', 2);

    cy.get('#tab-zoom .toolbar--multiline > a')
        .eq(0)
        .should('have.text', 'show all commit messages');

    cy.get('#tab-zoom .toolbar--multiline > a')
        .eq(1)
        .should('have.text', 'hide all commit messages');
  });

  it('check show all and hide all commit messages only toggle current commits', () => {
    // Assumptions: the third commit (19e3294) of the first author of the first repo
    // contains changes in only .java files.
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
        .should('be.visible');

    // uncheck java file type
    cy.get('#tab-zoom .fileTypes input[value="java"]')
        .uncheck()
        .should('not.be.checked');

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline')
        .click();

    // should only display 'show all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
        .children()
        .should('have.length', 1);

    cy.get('#tab-zoom .toolbar--multiline > a')
        .should('have.text', 'show all commit messages');

    // check java file type
    cy.get('#tab-zoom .fileTypes input[value="java"]')
        .check()
        .should('be.checked');

    // commit body of the third commit should be visible
    cy.get('#tab-zoom .commit-message .body')
        .eq(2)
        .should('be.visible');

    // should now display both 'hide all & show all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
        .children()
        .should('have.length', 2);

    cy.get('#tab-zoom .toolbar--multiline > a')
        .eq(0)
        .should('have.text', 'show all commit messages');

    cy.get('#tab-zoom .toolbar--multiline > a')
        .eq(1)
        .should('have.text', 'hide all commit messages');
  });
});
