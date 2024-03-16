describe('credit background colour', () => {
  it('check if background colour match the credit information for Eugene', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('exist')
      .first()
      .click();

    // full credit - #C8E6C9
    cy.get(':nth-child(1) > .file-content > .segment-collection > :nth-child(2) > .java > .code')
        .should('have.css', 'background-color')
        .and('eq', 'rgb(200, 230, 201)');

    // partial credit - #E6FFED
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

    // full credit - #F0808050
    cy.get(':nth-child(1) > .file-content > .segment-collection > :nth-child(7) > .scss > .code')
        .should('have.css', 'background-color')
        .and('eq', 'rgba(240, 128, 128, 0.314)');

    // partial credit - #1E90FF20
    cy.get(':nth-child(3) > .file-content > .segment-collection > :nth-child(10) > .java > .code')
        .should('have.css', 'background-color')
        .and('eq', 'rgba(30, 144, 255, 0.125)');
  });
});
