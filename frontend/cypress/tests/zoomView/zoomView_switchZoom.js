describe('switch zoom', () => {
  it('switch zoom view should restore all default controls', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    // switch zoom view
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .last()
        .click();

    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
        .should('have.value', 'time');

    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
        .should('have.value', 'true');

    cy.get('#tab-zoom > .fileTypes input[value="all"]')
        .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
        .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="java"]')
        .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="md"]')
        .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="yml"]')
        .should('be.checked');
  });
});
