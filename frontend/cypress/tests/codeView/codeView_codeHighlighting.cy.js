// Assumes: RepoSense repo from 03/05/2018 to current date
describe('code highlighting works properly', () => {
  it('should highlight code when there is a single author', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
    
    cy.get('.hljs-comment').contains('* Represents a Git Author.')
      .parent() // .line-content
      .parent() // .code
      .should('have.css', 'background-color', 'rgb(230, 255, 237)'); // #e6ffed
  });

  it('should highlight code when multiple authors are merged in a repo group', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    cy.get('.hljs-comment').contains('* MUI Colors module') // eugenepeh
      .parent() // .line-content
      .parent() // .code
      .should('have.css', 'background-color')
      .and('not.eq', 'rgb(255, 255, 255)') // #ffffff
      .then((color) => {
        cy.get('.line-content').contains(`'red': (`) // jamessspanggg
          .parent() // .code
          .should('have.css', 'background-color')
          .and('not.eq', color)
          .and('not.eq', 'rgb(255, 255, 255)')
      });
  });

  it('should not highlight non-attributed lines', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');
    
    cy.get('.hljs-title').contains('Author')
      .parent() // .hljs-class
      .parent() // .line-content
      .parent() // .code
      .should('have.css', 'background-color', 'rgb(255, 255, 255)'); // #ffffff
  });
});