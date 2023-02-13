describe('switch authorship', () => {
  it('switch authorship view should restore all default controls', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // change sort by
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
      .select('Path')
      .should('have.value', 'path');

    // change sort order
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
      .select('Ascending')
      .should('have.value', 'false');

    // uncheck a file type
    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
      .uncheck()
      .should('not.be.checked');

    // switch authorship view
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .last()
      .click();

    // check default controls
    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-by > select')
      .should('not.have.value', 'path')
      .should('have.value', 'lineOfCode');

    cy.get('#tab-authorship > .title > .contribution > .sorting > .sort-order > select')
      .should('have.value', 'true');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes > .radio-button--checkbox')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="all"]')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="gradle"]')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="java"]')
      .should('be.checked');

    cy.get('#tab-authorship > .title > .contribution > .fileTypes input[id="yml"]')
      .should('be.checked');
  });

  it('switch authorship view should not retain information from previous visited tabs', () => {
    // Assumptions:
    // The first repository has more than one person listed.
    // The first displayed file which the first and last person worked on is different.
    const betweenBracketsRegex = /\((.*)\)/;

    let firstAuthor;
    let firstFilename;
    let lastAuthor;

    // open the first code panel
    cy.get('#summary-charts > .summary-charts')
      .should('be.visible')
      .first()
      .find('.fa-code')
      .first()
      .click();

    cy.get('#tab-authorship > .panel-heading > .author > span')
      .last()
      .then(($span) => {
        firstAuthor = $span.text().match(betweenBracketsRegex).pop();
        cy.url()
          .should('include', firstAuthor);
      });

    cy.get('#tab-authorship > .files > .file > .title > .path')
      .first()
      .then(($span) => {
        firstFilename = $span.text();
      });

    // switch authorship view
    cy.get('#summary-charts > .summary-charts')
      .should('be.visible')
      .first()
      .find('.fa-code')
      .last()
      .click();

    cy.get('#tab-authorship > .panel-heading > .author > span')
      .last()
      .should(($span) => {
        lastAuthor = $span.text().match(betweenBracketsRegex).pop();
        expect(firstAuthor, 'First author to have different name from the last author')
          .to.not.equal(lastAuthor);
      })
      .then(() => {
        cy.url()
          .should('not.include', firstAuthor)
          .should('include', lastAuthor);
      });

    cy.get('#tab-authorship > .files > .file > .title > .path')
      .first()
      .should(($span) => {
        const lastFilename = $span.text();
        expect(firstFilename, 'First displayed filenames should be different for different authors')
          .to.not.equal(lastFilename);
      });
  });
});
