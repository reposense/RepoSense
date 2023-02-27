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

  it('should only display hide all commit messages when none are hidden', () => {
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

    cy.get('#tab-zoom .toolbar--multiline > a')
      .should('have.text', 'hide all commit messages');
  });

  it('should only display show all commit messages when all are hidden', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
      .should('be.visible');

    // hides all the commit messages
    cy.get('#tab-zoom .toolbar--multiline > a')
      .click();

    // should only display 'show all commit messages'
    cy.get('#tab-zoom .toolbar--multiline')
      .children()
      .should('have.length', 1);

    cy.get('#tab-zoom .toolbar--multiline > a')
      .should('have.text', 'show all commit messages');
  });

  it('should display both show and hide all commit messages when some are hidden', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
      .should('be.visible');

    // hide the message body of the first commit
    cy.get('#tab-zoom .commit-message > a .tooltip')
      .should('be.visible')
      .first()
      .click();

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

  it('check hidden commit message persists after sort', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message', { timeout: 90000 })
      .should('be.visible');

    // hide the message body of the first commit
    cy.get('#tab-zoom .commit-message > a .tooltip')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message .body')
      .first()
      .should('not.be.visible');

    cy.get('#tab-zoom .commit-message .hash')
      .first()
      .invoke('text')
    // keep track of first commit by hash so test doesn't rely on correctness of sort
      .then((hash) => {
        // change sort by
        cy.get('#tab-zoom > .sorting > .sort-by > select')
          .select('LoC')
          .should('have.value', 'lineOfCode');

        // message body should still be hidden
        cy.contains('#tab-zoom .commit-message', hash)
          .children('.body')
          .should('not.be.visible');

        // change sort order
        cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
          .select('Ascending')
          .should('have.value', 'false');

        // message body should still be hidden
        cy.contains('#tab-zoom .commit-message', hash)
          .children('.body')
          .should('not.be.visible');
      });
  });
});
