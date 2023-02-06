describe('check file types ', () => {
  it('check if all file types are visible by default', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .zoom__day', { timeout: 90000 })
      .should('exist');

    cy.get('#tab-zoom .fileTypes input[value="all"]')
      .should('be.checked');
  });

  it('uncheck all file types should show no files', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .zoom__day')
      .should('exist');

    cy.get('#tab-zoom .fileTypes input[value="all"]')
      .uncheck();

    cy.get('#tab-zoom .zoom__day')
      .should('not.exist');
  });

  it('uncheck file type should uncheck all option', () => {
    // Assumptions: the first author of the first repo
    // committed .java, .js and .gradle files.
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom .fileTypes input[value="java"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-zoom .fileTypes input[value="js"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('#tab-zoom .fileTypes input[value="all"]')
      .should('not.be.checked');
  });

  it('a commit should not be seen when all of its file types are unchecked', () => {
    // Assumptions: the third commit (19e3294) of the first author of the first repo
    // contains changes in only .java files.
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('.zoom__day > .commit-message')
      .eq(2)
      .within(() => {
        cy.get('.hash')
          .should('have.text', '19e3294');
        cy.get('.fileTypeLabel')
          .should('have.text', 'java');
      });

    cy.get('#tab-zoom .fileTypes input[value="java"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('.zoom__day > .commit-message')
      .should('not.contain.text', '19e3294');
  });

  it.only('unchecked file type label can still remain in a commit of multiple file types', () => {
    // Assumptions: the sixth commit (5ab0322) of the first author of the first repo
    // contains changes in both .java and .md files.
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('.zoom__day > .commit-message')
      .eq(5)
      .within(() => {
        cy.get('.hash')
          .should('have.text', '5ab0322');
        cy.get('.fileTypeLabel')
          .should('contain.text', 'java')
          .should('contain.text', 'md');
      });

    cy.get('#tab-zoom .fileTypes input[value="java"]')
      .uncheck()
      .should('not.be.checked');

    cy.get('.zoom__day > .commit-message')
    // note that the same commit is now of second order since other commits
    // were filtered out
      .eq(2)
      .within(() => {
        cy.get('.hash')
          .should('have.text', '5ab0322');
        cy.get('.fileTypeLabel')
          .should('contain.text', 'java')
          .should('contain.text', 'md');
      });
  });
});
