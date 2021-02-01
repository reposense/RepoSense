describe('switch authorship', () => {
  it('switch authorship view should have all file types checked', () => {
    Cypress.wait();

    // open the code panel
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
        .should('be.checked');

    // switch authorship view
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .last()
        .click();

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
        .should('be.checked');
  });
});
