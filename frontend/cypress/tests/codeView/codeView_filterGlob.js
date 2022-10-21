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
        .should('not.be.checked')
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

  it('check filter nothing should show all file types', () => {
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click();

    cy.get('.radio-button--search')
        .should('be.visible')
        .click();

    // enter on empty input
    cy.get('#search')
        .type('{enter}');

    cy.get('#tab-authorship .files', { timeout: 90000 })
        .should('be.visible');
  });

  it('check invalid glob should not show any files', () => {
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
        .type('invalid glob')

    // click 'Filter' button
    cy.get('#submit-button')
        .click();

    // no file should be shown
    cy.get('#tab-authorship .files')
        .should('not.be.visible');
  });
});
