describe('show tags', () => {
  it('unchecked should not display any tags for a group', () => {
    cy.get('#summary label.show-tags > input:visible')
      .should('be.visible')
      .uncheck()
      .should('not.be.checked');

    cy.get('.summary-charts__title--tags')
      .should('not.exist');
  });

  it('checked should display all tags for a group', () => {
    cy.get('#summary label.show-tags > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    const correctTags = [];

    cy.get('.zoom__title--tags')
      .find('.tag')
      .each(($tag) => correctTags.push($tag.text().trim()))
      .then(() => {
        cy.get('.summary-charts')
          .first()
          .find('.summary-charts__title--tags')
          .find('.tag')
          .each(($tag) => {
            expect(correctTags).to.include($tag.text().trim());
          });

        cy.get('.summary-charts')
          .first()
          .find('.summary-charts__title--tags')
          .find('.tag')
          .should('have.length', correctTags.length);
      });
  });

  it('clicked should redirect to the correct tag page', () => {
    cy.get('#summary label.show-tags > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('.summary-charts__title--tags')
      .find('.tag')
      .first()
      .invoke('removeAttr', 'target') // to open in the same window
      .click();

    cy.origin('https://github.com', () => {
      cy.url()
        .should('equal', 'https://github.com/reposense/RepoSense/releases/tag/v1.0');
    });
  });

  it('group by authors works with show tags', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByAuthors');

    cy.get('div.mui-select.sort-within-group > select:visible')
      .select('title dsc');

    cy.get('#summary label.show-tags > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('.summary-chart')
      .first()
      .find('.summary-chart__title--tags')
      .find('a')
      .should('have.length', 0);

    cy.get('.summary-chart')
      .eq(1)
      .find('.summary-chart__title--tags')
      .find('a')
      .should('have.length.gt', 0);

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .eq(1)
      .click();

    const correctTags = [];

    cy.get('.zoom__title--tags')
      .find('.tag')
      .each(($tag) => correctTags.push($tag.text().trim()))
      .then(() => {
        cy.get('.summary-chart')
          .eq(1)
          .find('.summary-chart__title--tags')
          .find('.tag')
          .each(($tag) => {
            expect(correctTags).to.include($tag.text().trim());
          });

        cy.get('.summary-chart')
          .eq(1)
          .find('.summary-chart__title--tags')
          .find('.tag')
          .should('have.length', correctTags.length);
      });
  });

  it('group by none works with show tags', () => {
    cy.get('div.mui-select.grouping > select:visible')
      .select('groupByNone');

    cy.get('#summary label.show-tags > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    const correctTags = [];
  
    cy.get('.zoom__title--tags')
      .find('.tag')
      .each(($tag) => correctTags.push($tag.text().trim()))
      .then(() => {
        cy.get('.summary-chart')
          .first()
          .find('.summary-chart__title--tags')
          .find('.tag')
          .each(($tag) => {
            expect(correctTags).to.include($tag.text().trim());
          });
  
        cy.get('.summary-chart')
          .first()
          .find('.summary-chart__title--tags')
          .find('.tag')
          .should('have.length', correctTags.length);
      });
  });
});
