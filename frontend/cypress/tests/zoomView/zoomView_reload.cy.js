describe('reload page', () => {
  it('reload page should restore all controls', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // change sort by
    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .select('LoC');

    // change sort order
    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .select('Ascending');

    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="scss"]')
      .uncheck()
      .should('not.be.checked');

    cy.reload();

    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .should('have.value', 'lineOfCode');

    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .should('have.value', 'false');

    cy.get('#tab-zoom > .fileTypes input[value="all"]')
      .should('not.be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
      .should('not.be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="scss"]')
      .should('not.be.checked');
  });
});
