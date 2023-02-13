describe('filter glob', () => {
  it('check filter glob radio button is clickable', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // click on filter glob radio button
    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // filter by file type should be unchecked
    cy.get('.radio-button--checkbox')
      .should('not.be.checked');
  });

  it('check no filter glob input should show all file types by default', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // the contents of all the files should be visible
    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
  });

  it('check filter glob input should be in focus after click', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // click on input box
    cy.get('#search')
      .click()
      .should('have.focus');
  });

  it('check filter glob input should have default empty value', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    cy.get('#search')
      .should('have.value', '');
  });

  it('check deleting previously searched input to filter empty input by enter should show all file types', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // enter some input
    cy.get('#search')
      .type('an input');

    // submit some input
    cy.get('#search')
      .type('{enter}');

    // delete previous input
    cy.get('#search')
      .clear();

    // enter on empty input
    cy.get('#search')
      .type('{enter}');

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
  });

  it('check deleting previously searched input to filter empty input by clicking should show all file types', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    cy.get('#search')
      .type('an input');

    cy.get('#submit-button')
      .click();

    cy.get('#search')
      .clear();

    // click 'Filter' on empty input
    cy.get('#submit-button')
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
  });

  it('check request to filter invalid glob by enter should not show any files', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#search')
      .type('invalid glob');

    cy.get('#search')
      .type('{enter}');

    // no file should be shown
    cy.get('#tab-authorship .files')
      .should('not.be.visible');
  });

  it('check request to filter invalid glob by clicking should not show any files', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('#search')
      .type('invalid glob');

    cy.get('#submit-button')
      .click();

    cy.get('#tab-authorship .files')
      .should('not.be.visible');
  });

  it('check filter glob should only show files with that extension', () => {
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('.radio-button--search')
      .should('be.visible')
      .click();

    // try java
    cy.get('#search')
      .type('*java');

    cy.get('#submit-button')
      .click();

    cy.get('#tab-authorship .files').then(($files) => {
      // check if there is any file with .java extension
      if ($files.hasClass('path')) {
        // check each file for .java extension
        cy.get('.title > .path > span')
          .then(($spans) => {
            $spans.toArray().forEach((span) => {
              cy.wrap(span).contains('.java');
            });
          });
      }
    });
  });
});
