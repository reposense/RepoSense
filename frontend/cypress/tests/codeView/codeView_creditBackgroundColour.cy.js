describe('credit background colour', () => {
  it('check if background colour match the credit information for Eugene', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    // src/main/java/reposense/model/Author.java
    // line 9: full credit - #BFF6CF
    cy.get(':nth-child(1) > .file-content > .segment-collection > :nth-child(2) > .java > .code')
      .should('have.css', 'background-color')
      .and('eq', 'rgb(191, 246, 207)');

    // src/main/java/reposense/model/Author.java
    // line 15: partial credit - #E6FFED
    cy.get(':nth-child(1) > .file-content > .segment-collection > :nth-child(4) > .java > .code')
      .should('have.css', 'background-color')
      .and('eq', 'rgb(230, 255, 237)');
  });

  it('check if background colour match the credit information when group is merged', () => {
    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .click();

    // frontend/src/styles/_colors.scss
    // line 35: full credit - #F0808050
    cy.get(':nth-child(7) > .scss > :nth-child(1)')
      .should('have.css', 'background-color')
      .and('eq', 'rgba(240, 128, 128, 0.314)');

    // FileInfoExtractor.java is too far away to be loaded, use filter to go to it directly
    cy.get('#search')
      .click()
      .type('FileInfoExtractor.java');

    cy.get('#submit-button')
      .click();

    // src/main/java/reposense/authorship/FileInfoExtractor.java
    // line 23: partial credit - #1E90FF20
    cy.get(':nth-child(10) > .java > :nth-child(1)')
      .should('have.css', 'background-color')
      .and('eq', 'rgba(30, 144, 255, 0.125)');
  });
});
