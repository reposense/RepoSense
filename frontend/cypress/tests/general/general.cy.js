describe('general', () => {
  it('correctly replaces report title', () => {
    cy.title().should('eq', 'RepoSense Test Report');
  });

  it('correctly contains given title', () => {
    cy.get('h1').should('contain', 'RepoSense Intro');
  });
});
