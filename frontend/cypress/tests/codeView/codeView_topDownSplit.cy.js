const TOLERANCE = 0.05;
const WAITING_DELAY = 200;
const LANDSCAPE_VIEWPORT = { width: 1200, height: 800 };
const PORTRAIT_VIEWPORT  = { width: 600,  height: 800 };
const DEFAULT_RATIO = 0.5;

describe('Viewport and Orientation Tests', () => {
  it('switches to portrait layout on narrow viewport', () => {
    // Test landscape first
    cy.viewport(LANDSCAPE_VIEWPORT.width, LANDSCAPE_VIEWPORT.height);
    cy.get('.icon-button.fa-code').first().click();

    cy.get('#app-wrapper')
      .should('have.css', 'flex-direction', 'row');

    // Verify container arrangement in landscape
    cy.get('.left-resize-container').should('be.visible');
    cy.get('.right-resize-container').should('be.visible');

    // Switch to portrait
    cy.viewport(PORTRAIT_VIEWPORT.width, PORTRAIT_VIEWPORT.height);

    cy.get('#app-wrapper')
      .should('have.css', 'flex-direction', 'column');
  });

  it('resizer cursor changes between orientations', () => {
    cy.get('.icon-button.fa-code').first().click();

    // Landscape: column resize cursor
    cy.viewport(LANDSCAPE_VIEWPORT.width, LANDSCAPE_VIEWPORT.height);
    cy.get('#tab-resize')
      .should('have.css', 'cursor', 'col-resize');

    // Portrait: row resize cursor
    cy.viewport(PORTRAIT_VIEWPORT.width, PORTRAIT_VIEWPORT.height);
    cy.get('#tab-resize')
      .should('have.css', 'cursor', 'row-resize');
  });

  it('maintains separate ratios for portrait and landscape', () => {
    // Start in landscape and adjust the resize ratio
    cy.viewport(LANDSCAPE_VIEWPORT.width, LANDSCAPE_VIEWPORT.height);
    cy.get('.icon-button.fa-code').first().click();

    // Drag landscape resizer to 30% width
    cy.get('#tab-resize')
      .trigger('mousedown', { button: 0 });

    // 30% of 1200px
    cy.get('#tab-resize')
      .trigger('mousemove', { clientX: (0.3 * LANDSCAPE_VIEWPORT.width) });

    // Release mouse
    cy.get('#tab-resize')
      .trigger('mouseup');

    // Wait for resize to complete and verify it actually happened
    cy.wait(WAITING_DELAY);
    cy.get('.left-resize-container').invoke('outerWidth').as('actualLandscapeWidth');
    cy.get('#app-wrapper').invoke('outerWidth').as('totalLandscapeWidth');
    cy.then(function checkLandscapeResize() {
      const actualRatio = this.actualLandscapeWidth / this.totalLandscapeWidth;
      expect(actualRatio).to.be.closeTo(0.3, TOLERANCE);
    });


    // Save current dimensions
    cy.get('.left-resize-container').invoke('outerWidth').as('landscapeWidth');
    cy.get('#app-wrapper').invoke('outerWidth').as('totalWidth');

    // Switch to portrait
    cy.viewport(PORTRAIT_VIEWPORT.width, PORTRAIT_VIEWPORT.height);
    cy.wait(WAITING_DELAY);

    cy.get('.left-resize-container').invoke('outerHeight').as('portraitHeight');
    cy.get('#app-wrapper').invoke('outerHeight').as('totalHeight');

    // The portrait height should not reflect the 30% from landscape, should be the default 50%
    cy.then(function checkHeight() {
      const currentRatio = this.portraitHeight / this.totalHeight;
      expect(currentRatio).to.be.closeTo(DEFAULT_RATIO, TOLERANCE);
    });

    // Resize portrait to 70%
    cy.get('#tab-resize')
      .trigger('mousedown', { button: 0 });
    cy.get('#tab-resize')
      .trigger('mousemove', { clientY: (0.7 * PORTRAIT_VIEWPORT.height) }); // 70% of 800px
    cy.get('#tab-resize')
      .trigger('mouseup');

    // Wait and check portrait resize is done
    cy.wait(WAITING_DELAY);
    cy.get('.left-resize-container').invoke('outerHeight').as('actualPortraitHeight');
    cy.get('#app-wrapper').invoke('outerHeight').as('totalPortraitHeight');
    cy.then(function checkPortraitResize() {
      const actualRatio = this.actualPortraitHeight / this.totalPortraitHeight;
      expect(actualRatio).to.be.closeTo(0.7, TOLERANCE);
    });

    // Switch back to landscape – should still be at 30%
    cy.viewport(LANDSCAPE_VIEWPORT.width, LANDSCAPE_VIEWPORT.height);
    cy.wait(WAITING_DELAY);

    cy.get('.left-resize-container').invoke('outerWidth').as('currentLandscapeWidth');
    cy.then(function checkLandscapeWidth() {
      const landscapeRatio = this.landscapeWidth / this.totalWidth;
      const currentRatio = this.currentLandscapeWidth / this.totalWidth;
      expect(currentRatio).to.be.equals(landscapeRatio);
    });
  });
});