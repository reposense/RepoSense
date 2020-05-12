describe('filter breakdown', () => {
  it('check breakdown by file type should show file types', () => {
    Cypress.wait();

    cy.get('#summary label.filter-breakdown input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

    // should show file type checkboxes
    cy.get('#summary div.fileTypes')
        .should('be.visible');

    // should show file type legends
    cy.get('#summary-charts div.summary-charts__fileType--breakdown')
        .should('be.visible');
  });

  it('uncheck all file types should show no file types', () => {
    Cypress.wait();

    cy.get('#summary label.filter-breakdown input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

    // uncheck all file types
    cy.get('#summary div.fileTypes input:visible[id="all"]')
        .uncheck()
        .should('not.be.checked');

    // should not show file type legends
    cy.get('#summary-charts div.summary-charts__fileType--breakdown')
        .should('not.be.visible');
  });

  it('uncheck file type should uncheck all option and not show legend', () => {
    Cypress.wait();

    cy.get('#summary label.filter-breakdown input:visible')
        .should('be.visible')
        .check()
        .should('be.checked');

    // uncheck gradle file type
    cy.get('#summary div.fileTypes input[id="gradle"]')
        .uncheck()
        .should('not.be.checked');

    // should not show gradle file type legend
    cy.get('#summary-charts > div > div.summary-charts__fileType--breakdown span')
        .should('not.contain.text', 'gradle');

    // all option should be unchecked
    cy.get('#summary div.fileTypes input[id="all"]')
        .should('not.be.checked');
  });
});
