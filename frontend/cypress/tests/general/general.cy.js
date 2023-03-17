describe('general', () => {
  it('correctly replaces report title', () => {
    cy.title().should('eq', 'RepoSense Test Report');
  });
});
