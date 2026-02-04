describe('filter mode dropdown', () => {
  it('should exist with Global and Local options', () => {
    cy.get('#app #tab-resize .tab-close').click();

    // Verify filter mode dropdown exists
    cy.get('#summary .mui-select.filter-mode')
      .should('exist');

    // Verify it has a label
    cy.get('#summary .mui-select.filter-mode label')
      .should('contain', 'filter mode');

    // Verify the select element exists
    cy.get('#summary .mui-select.filter-mode select')
      .should('exist');

    // Verify it has Local option
    cy.get('#summary .mui-select.filter-mode select option[value="local"]')
      .should('exist')
      .and('contain', 'Local');

    // Verify it has Global option
    cy.get('#summary .mui-select.filter-mode select option[value="global"]')
      .should('exist')
      .and('contain', 'Global');

    // Verify default value is Local
    cy.get('#summary .mui-select.filter-mode select')
      .should('have.value', 'local');
  });

  it('should switch to global mode and open file browser tab', () => {
    cy.get('#app #tab-resize .tab-close').click();

    // Switch filter mode to "Global"
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    // Wait for the tab to be activated and visible
    cy.get('#tab-file-browser')
      .should('be.visible');

    // Verify the header exists
    cy.get('#tab-file-browser .header h3')
      .should('contain', 'Global File Browser');
  });

  it('should switch back to local mode', () => {
    // Switch to global mode
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    // Wait for file browser to be visible
    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    // Switch back to local mode
    cy.get('#summary .mui-select.filter-mode select')
      .select('Local');

    // Verify file browser is no longer visible
    cy.get('#tab-file-browser')
      .should('not.exist');

    // Verify we're back to the welcome tab
    cy.get('#tab-empty')
      .should('be.visible');
  });
});
