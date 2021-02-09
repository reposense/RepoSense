describe('switch zoom', () => {
  it('switch zoom view should restore all default controls', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    // change sort by
    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
        .select('LoC')
        .should('have.value', 'lineOfCode');

    // change sort order
    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
        .select('Ascending')
        .should('have.value', 'false');

    // uncheck a file type
    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
        .uncheck()
        .should('not.be.checked');

    // switch zoom view
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .last()
        .click();

    // check default controls
    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
        .should('not.have.value', 'lineOfCode')
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

  it('switch zoom view should not retain information from previous visited tabs', () => {
    Cypress.wait();

    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();

    cy.get('#tab-zoom .panel-heading .author span')
        .should('contain.text', 'eugenepeh');

    cy.get('#tab-zoom .zoom__day h3')
        .should('contain.text', '2019-12-24');

    // switch zoom view
    cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .last()
        .click();

    cy.get('#tab-zoom .panel-heading .author span')
        .should('not.contain.text', 'eugenepeh')
        .should('contain.text', 'yong24s');

    cy.get('#tab-zoom .zoom__day h3')
        .should('not.contain.text', '2019-12-24')
        .should('contain.text', '2019-06-10');
  });
});

