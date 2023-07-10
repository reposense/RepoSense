describe('include merge commits in chart view', () => {
  it('show merge commits in summary chart', () => {
    // ramp chart should have merge commit slices
    cy.get('[title="[2023-03-04] Merge branch \'new-branch\' into cypress: +0 -0 lines "]')
      .should('have.class', 'ramp__slice')
      .should('exist');
  });

  it('hide merge commits in summary chart when some file types unselected', () => {
    // check breakdown by file type
    cy.get('#summary label.filter-breakdown input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // uncheck gradle file type
    cy.get('#summary div.fileTypes input[id="gradle"]')
      .uncheck()
      .should('not.be.checked');

    // ramp chart should not have merge commit slices
    cy.get('[title="[2023-03-04] Merge branch \'new-branch\' into cypress: +0 -0 lines "]')
      .should('not.exist');
  });
});
