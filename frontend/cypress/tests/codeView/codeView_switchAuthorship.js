describe('switch authorship', () => {
  it('switch authorship view should restore all default controls', () => {
    Cypress.wait();

    // open the code panel
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click();

    // change sort by
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
        .select('Path')
        .should('have.value', 'path');

    // change sort order
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
        .select('Ascending')
        .should('have.value', 'false');

    // uncheck a file type
    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
        .uncheck()
        .should('not.be.checked');

    // switch authorship view
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .last()
        .click();

    // check default controls
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
        .should('not.have.value', 'path')
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

  it('switch authorship view should not retain information from previous visited tabs', () => {
    Cypress.wait();

    // open the code panel
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-authorship .panel-heading .author span')
        .should('contain.text', 'eugenepeh');

    cy.get('#tab-authorship .title .path')
        .first()
        .should('contain.text', 'frontend/src/static/js/v_summary.js');

    cy.url()
        .should('include', 'eugenepeh');

    // switch authorship view
    cy.get('.icon-button.fa-code')
        .should('be.visible')
        .last()
        .click();

    cy.get('#tab-authorship .panel-heading .author span')
        .should('not.contain.text', 'eugenepeh')
        .should('contain.text', 'yong24s');

    cy.get('#tab-authorship .title .path')
        .first()
        .should('contain.text', 'src/test/java/reposense/parser/ArgsParserTest.java');

    cy.url()
        .should('not.include', 'eugenepeh')
        .should('include', 'yong24s');
  });
});
