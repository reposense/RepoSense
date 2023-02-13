describe('hide all file details', () => {
  it('check hide all file details hides the content of all the files', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    // the contents of all the files should be visible
    cy.get('#tab-authorship .file-content ')
      .should('be.visible');

    // hide the details of all the files
    cy.get('#tab-authorship .toolbar--multiline')
      .should('be.visible')
      .click();

    cy.get('#tab-authorship .files')
      .should('be.visible');

    // the contents of all the files should be hidden
    cy.get('#tab-authorship .file-content ')
      .should('not.be.visible');
  });

  it('check details of one file are shown, rest are hidden', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship .toolbar--multiline')
      .should('be.visible')
      .click();

    // should show 'show all file details' only
    cy.get('#tab-authorship .toolbar--multiline a')
      .should('not.contain.text', 'hide all file details')
      .should('contain.text', 'show all file details');

    // open contents of the first file
    cy.get('#tab-authorship .title .caret')
      .should('be.visible')
      .first()
      .click();

    // should show both 'show/hide all file details'
    cy.get('#tab-authorship .toolbar--multiline a')
      .should('contain.text', 'hide all file details')
      .should('contain.text', 'show all file details');

    // contents of the first file should be visible
    cy.get('#tab-authorship .file-content ')
      .first()
      .should('be.visible');

    // contents of the last file should be hidden
    cy.get('#tab-authorship .file-content ')
      .last()
      .should('not.be.visible');
  });

  it('check show all file details shows the content of all the files', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship .file-content ')
      .should('be.visible');

    // hide the content of all the files
    cy.get('#tab-authorship .toolbar--multiline')
      .should('be.visible')
      .click();

    cy.get('#tab-authorship .file-content ')
      .should('not.be.visible');

    // show the content of all the files
    cy.get('#tab-authorship .toolbar--multiline')
      .should('be.visible')
      .click();

    cy.get('#tab-authorship .file-content ')
      .should('be.visible');
  });

  it('check collapsed file persists after sort', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#tab-authorship .file-content ')
      .first()
      .should('be.visible');

    // hide contents of the first file
    cy.get('#tab-authorship .title .caret')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .file-content ')
      .first()
      .should('not.be.visible');

    cy.get('#tab-authorship .title .path')
      .first()
      .invoke('text')
    // keep track of first file by file path so test doesn't rely on correctness of sort
      .then((filePath) => {
        // change sort by
        cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
          .select('Path')
          .should('have.value', 'path');

        // wait until loading is finished
        cy.get('[aria-label="Loading"]')
          .should('not.be.visible');

        // contents of file should still be hidden
        cy.contains('#tab-authorship .file', filePath.trim())
          .children('.file-content')
          .should('not.be.visible');

        // change sort order
        cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
          .select('Ascending')
          .should('have.value', 'false');

        // wait until loading is finished
        cy.get('[aria-label="Loading"]')
          .should('not.be.visible');

        // contents of file should still be hidden
        cy.contains('#tab-authorship .file', filePath.trim())
          .children('.file-content')
          .should('not.be.visible');
      });
  });
});
