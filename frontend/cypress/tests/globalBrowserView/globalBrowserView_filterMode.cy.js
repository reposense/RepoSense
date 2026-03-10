describe('global browser view - filter mode', () => {
  it('should have filter mode dropdown with Global and Local options', () => {
    cy.get('#app #tab-resize .tab-close').click();

    cy.get('#summary .mui-select.filter-mode')
      .should('exist');

    cy.get('#summary .mui-select.filter-mode label')
      .should('contain', 'filter mode');

    cy.get('#summary .mui-select.filter-mode select option[value="local"]')
      .should('exist')
      .and('contain', 'Local');

    cy.get('#summary .mui-select.filter-mode select option[value="global"]')
      .should('exist')
      .and('contain', 'Global');

    cy.get('#summary .mui-select.filter-mode select')
      .should('have.value', 'local');
  });

  it('should have filter mode tooltip', () => {
    cy.get('#summary .mui-select.filter-mode')
      .closest('.tooltip')
      .should('exist');
  });

  it('should switch from welcome tab to file browser tab when selecting Global', () => {
    cy.get('#tab-empty')
      .should('be.visible');

    cy.get('#tab-file-browser')
      .should('not.exist');

    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('#tab-file-browser .header h3')
      .should('contain', 'Global File Browser');

    cy.get('#tab-empty')
      .should('not.exist');
  });

  it('should switch from file browser tab back to welcome tab when selecting Local', () => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('#summary .mui-select.filter-mode select')
      .select('Local');

    cy.get('#tab-empty')
      .should('be.visible');

    cy.get('#tab-file-browser')
      .should('not.exist');
  });

  it('should deselect highlighted author when switching to Global mode', () => {
    // Open authorship tab for the first author
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-authorship', { timeout: 90000 })
      .should('be.visible');

    cy.get('.icon-button.fa-code.active-icon')
      .should('have.length.greaterThan', 0);

    // Switch to Global mode
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    // No author should be highlighted
    cy.get('.icon-button.fa-code.active-icon')
      .should('have.length', 0);
  });

  it('should switch filter mode back to Local when clicking authorship icon', () => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-authorship', { timeout: 90000 })
      .should('be.visible');

    cy.get('#summary .mui-select.filter-mode select')
      .should('have.value', 'local');

    cy.get('#tab-file-browser')
      .should('not.exist');
  });

  it('should switch filter mode back to Local when clicking zoom icon', () => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom', { timeout: 10000 })
      .should('be.visible');

    cy.get('#summary .mui-select.filter-mode select')
      .should('have.value', 'local');

    cy.get('#tab-file-browser')
      .should('not.exist');
  });

  it('should preserve global file data when toggling Global mode off and on', () => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('#tab-file-browser .file-count')
      .invoke('text')
      .then((firstCount) => {
        cy.get('#summary .mui-select.filter-mode select')
          .select('Local');

        cy.get('#tab-empty')
          .should('be.visible');

        cy.get('#summary .mui-select.filter-mode select')
          .select('Global');

        cy.get('#tab-file-browser', { timeout: 10000 })
          .should('be.visible');

        cy.get('#tab-file-browser .file-count')
          .invoke('text')
          .should('eq', firstCount);
      });
  });

  it('should load files successfully on first switch to Global', () => {
    cy.get('#summary .mui-select.filter-mode select')
      .select('Global');

    cy.get('#tab-file-browser', { timeout: 10000 })
      .should('be.visible');

    cy.get('#tab-file-browser .file-list .file-item')
      .should('have.length.greaterThan', 0);
  });
});
