describe('switch authorship', () => {
  it('switch authorship view should restore all default controls', () => {
    Cypress.wait();

    // open the code panel
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click();

    // switch authorship view
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .last()
        .click();

    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
        .should('have.value', 'lineOfCode');

    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
        .should('have.value', 'true');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes > .radio-button--checkbox')
        .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
        .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
        .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="java"]')
        .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="yml"]')
        .should('be.checked');
  });
});
