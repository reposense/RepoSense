describe('reload page', () => {
  it('reload page should restore all controls', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // change sort by
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
      .select('LoC');

    // change sort order
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
      .select('Descending');

    // select radio-button
    cy.get('#tab-authorship > .title > .contribution > .fileTypes > .radio-button--checkbox')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="java"]')
      .should('be.checked');

    cy.reload();

    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
      .should('have.value', 'lineOfCode');

    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
      .should('have.value', 'true');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes > .radio-button--checkbox')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .should('not.be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
      .should('not.be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="java"]')
      .should('be.checked');
  });
});
