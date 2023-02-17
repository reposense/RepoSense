describe('switch zoom', () => {
  it('switch zoom view should restore all default controls', () => {
    // open the commit panel
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    // change sort by
    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .select('LoC')
      .should('have.value', 'lineOfCode');

    // change sort order
    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .select('Ascending')
      .should('have.value', 'false');

    // uncheck a file type
    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
      .uncheck()
      .should('not.be.checked');

    // switch zoom view
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .last()
      .click();

    // check default controls
    cy.get('#tab-zoom > .sorting > .sort-by > select:visible')
      .should('not.have.value', 'lineOfCode')
      .should('have.value', 'time');

    cy.get('#tab-zoom > .sorting > .sort-order > select:visible')
      .should('have.value', 'true');

    cy.get('#tab-zoom > .fileTypes input[value="all"]')
      .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="gradle"]')
      .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="java"]')
      .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="md"]')
      .should('be.checked');

    cy.get('#tab-zoom > .fileTypes input[value="yml"]')
      .should('be.checked');
  });

  it('switch zoom view should not retain information from previous visited tabs', () => {
    // Assumptions:
    // The first repository has more than one person listed.
    // The first day in which the first and last person contributed is different.
    const betweenBracketsRegex = /\((.*)\)/;

    let firstAuthor;
    let firstDay;
    let lastAuthor;

    // open the commit panel
    cy.get('#summary-charts > .summary-charts')
      .should('be.visible')
      .first()
      .find('.fa-list-ul')
      .first()
      .click();

    cy.get('#tab-zoom > .panel-heading > .author > span')
      .last()
      .then(($span) => {
        firstAuthor = $span.text().match(betweenBracketsRegex).pop();
        cy.url()
          .should('include', firstAuthor);
      });

    cy.get('#tab-zoom > .zoom__day > h3')
      .first()
      .then(($h3) => {
        firstDay = $h3.text();
      });

    // switch zoom view
    cy.get('#summary-charts > .summary-charts')
      .should('be.visible')
      .first()
      .find('.fa-list-ul')
      .last()
      .click();

    cy.get('#tab-zoom > .panel-heading > .author > span')
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

    cy.get('#tab-zoom > .zoom__day > h3')
      .first()
      .should(($h3) => {
        const lastDay = $h3.text();
        expect(firstDay, 'First displayed date should be different for different authors by assumptions')
          .to.not.equal(lastDay);
      });
  });
});
