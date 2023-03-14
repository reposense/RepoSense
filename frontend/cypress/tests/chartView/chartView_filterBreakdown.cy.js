describe('filter breakdown', () => {
  it('check breakdown by file type should show file types', () => {
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

  it('check, uncheck and recheck breakdown by file type should check all file types', () => {
    cy.get('#summary label.filter-breakdown input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // uncheck all file types
    cy.get('#summary div.fileTypes input:visible[id="all"]')
      .uncheck()
      .should('not.be.checked');

    cy.contains('breakdown by file type').scrollIntoView();

    // uncheck and recheck breakdown by file type
    cy.get('#summary label.filter-breakdown input:visible')
      .should('be.visible')
      .uncheck()
      .should('not.be.checked')
      .check()
      .should('be.checked');

    cy.get('#summary div.fileTypes input:visible[id="all"]')
      .should('be.checked');
  });

  it('uncheck all file types should show no file types', () => {
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
