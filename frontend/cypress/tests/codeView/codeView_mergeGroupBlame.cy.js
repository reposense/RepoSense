describe('merge group blame in code view', () => {
  it('no author breakdown shown by default', () => {
    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // code panel should be visible
    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    // author breakdown should not be visible
    cy.get('#tab-authorship .files .file .title .author-breakdown')
      .should('not.exist');
  });

  it('merge group for group by repos shows author breakdown for each file', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // code panel should be visible
    cy.get('#tab-authorship .files', { timeout: 90000 })
      .should('be.visible');

    // author breakdown should exist
    cy.get('#tab-authorship .files .file .title .author-breakdown')
      .should('exist');
  });

  it('author breakdown visible when file content hidden', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // hide content of all files
    cy.get('#tab-authorship .toolbar--multiline')
      .should('be.visible')
      .click();

    // author breakdown should be visible
    cy.get('#tab-authorship .files .file .title .author-breakdown')
      .should('be.visible');
  });

  it('author breakdown contains only file authors in alphabetical order', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // author breakdown contains expected author count and order
    const expectedResult = ['eugenepeh', 'jamessspanggg', 'yamidark', 'yong24s'];
    cy.get('#tab-authorship .files .file .title .author-breakdown')
      .eq(2)
      .children('.author-breakdown__legend')
      .should('have.length', expectedResult.length)
      .each(($el, index) => {
        cy.wrap($el).should('contain', expectedResult[index]);
      });
  });

  it('authors are assigned the same color in all files', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // check first and second occurence of same author has the same color
    cy.get('#tab-authorship .author-breakdown__legend:contains(\'jamessspanggg\')')
      .first()
      .then(($el) => {
        const color = $el.children().first().css('color');
        cy.get('#tab-authorship .author-breakdown__legend:contains(\'jamessspanggg\')')
          .eq(1).children().first()
          .should('have.css', 'color', color);
      });
  });

  it('author name shown on segment hover for known authors', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // author name in html title attribute should be shown
    cy.get('#tab-authorship .files .segment.active')
      .not('.untouched')
      .first()
      .invoke('attr', 'title')
      .should('contain', 'eugenepeh');
  });

  it('author name shown as unknown for missing authors', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // author name in html title attribute should be unknown
    cy.get('#tab-authorship .files .segment.untouched')
      .first()
      .invoke('attr', 'title')
      .should('contain', 'Unknown');
  });

  it('code segment has same color as author legend', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // segment color should be the same as author legend color
    cy.get('#tab-authorship .files .segment.active')
      .not('.untouched')
      .first()
      .then(($el) => {
        const color = $el.css('border-left-color');
        const author = 'eugenepeh';
        cy.get('#tab-authorship .files .file .title .author-breakdown')
          .eq(2)
          .children('.author-breakdown__legend')
          .contains(author)
          .prev()
          .should('have.css', 'color', color);
      });
  });

  it('colors in author breakdown match assigned colors', () => {
    // group summary charts by repos
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByRepos');

    // check merge group checkbox
    cy.get('#summary label.merge-group > input')
      .should('be.visible')
      .check()
      .should('be.checked');

    // open the code panel
    cy.get('.icon-button.fa-code')
      .should('be.visible')
      .first()
      .click();

    // author breakdown contains assigned colors
    const expectedResult = ['rgb(30, 144, 255)', 'rgb(240, 128, 128)', 'rgb(0, 255, 127)', 'rgb(255, 215, 0)'];
    cy.get('#tab-authorship .files .file .title .author-breakdown')
      .eq(2)
      .children('.author-breakdown__legend')
      .each(($el, index) => {
        cy.wrap($el).children().first().should('have.css', 'color', expectedResult[index]);
      });
  });
});
