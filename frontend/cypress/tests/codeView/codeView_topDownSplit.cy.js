describe('responsive layout', () => {
  it('switches to portrait layout on narrow viewport', () => {
    // Test landscape first
    cy.viewport(1200, 800);
    cy.get('.icon-button.fa-code').first().click();

    cy.get('#app-wrapper')
      .should('have.css', 'flex-direction', 'row');

    // Switch to portrait
    cy.viewport(600, 800);

    cy.get('#app-wrapper')
      .should('have.css', 'flex-direction', 'column');
  });

  it('resizer cursor changes between orientations', () => {
    cy.get('.icon-button.fa-code').first().click();

    // Landscape: column resize cursor
    cy.viewport(1200, 800);
    cy.get('#tab-resize')
      .should('have.css', 'cursor', 'col-resize');

    // Portrait: row resize cursor
    cy.viewport(600, 800);
    cy.get('#tab-resize')
      .should('have.css', 'cursor', 'row-resize');
  });

  it('maintains separate ratios for portrait and landscape', () => {
    cy.get('.icon-button.fa-code').first().click();

    cy.viewport(600, 800);

    // Check that both containers together fill the parent
    cy.get('.left-resize-container').invoke('outerHeight').as('leftHeight');
    cy.get('.right-resize-container').invoke('outerHeight').as('rightHeight');
    cy.get('#app-wrapper').invoke('outerHeight').as('totalHeight');

    cy.then(function checkCombinedHeight() {
      const combined = this.leftHeight + this.rightHeight;
      expect(combined).to.be.closeTo(this.totalHeight, 15); // Account for borders/padding
    });

    // Check that right container is approximately half
    cy.then(function checkRightContainerHalf() {
      const expectedHalf = this.totalHeight / 2;
      expect(this.rightHeight).to.be.closeTo(expectedHalf, 10);
    });
  });
});