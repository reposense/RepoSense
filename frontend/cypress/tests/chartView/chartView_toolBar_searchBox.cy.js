describe('search bar', () => {
  it('non-existent author shows no result', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('abcdef')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const content = $ele.html();

      expect(content).to.be.empty;
    });
  });

  it('unique author shows one result', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('Metta')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts').then(($ele) => {
      const children = $ele.children().length;
      expect(children).to.equal(1);
    });
  });

  it('searching by non-existent tag shows no results', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('tag: asdfghjkl')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts')
      .should('be.empty');
  });

  it("searching tag that only exists in one author's commits shows one result", () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('tag: v1.8')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('.summary-chart__title--name')
      .should('have.length', 1)
      .and('contain', 'Eugene (eugenepeh)');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('.zoom__title--tags > .tag span')
      .should('contain', 'v1.8');
  });

  it("searching tag that only exists in two authors' commits shows two results", () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('tag: v1.10')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('.summary-chart__title--name')
      .should('have.length', 2)
      .and('contain', 'Eugene (eugenepeh)')
      .and('contain', 'James (jamessspanggg)');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .each(($ele) => {
        cy.wrap($ele).click();
        cy.get('.zoom__title--tags > .tag span')
          .should('contain', 'v1.10');
      });
  });

  it("search field doesn't start with 'tag:' prefix but still contains it shows no results", () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('v1.10 tag: v1.10')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts')
      .should('be.empty');
  });

  it("search field doesn't contain 'tag:' at all shows no results", () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('v1.10')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('#summary-wrapper #summary-charts')
      .should('be.empty');
  });

  it('searching for multiple tags shows results containing all the tags searched', () => {
    cy.get('#app #tab-resize .tab-close').click();
    cy.get('#summary-wrapper input[type=text]')
      .eq(1)
      .type('tag: bb v1.10')
      .type('{enter}');

    cy.get('#summary-wrapper form.summary-picker')
      .submit();

    cy.get('.summary-chart__title--name')
      .should('have.length', 2)
      .and('contain', 'Eugene (eugenepeh)')
      .and('contain', 'James (jamessspanggg)');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('.zoom__title--tags > .tag span')
      .should('contain', 'bb');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .eq(1)
      .click();

    cy.get('.zoom__title--tags > .tag span')
      .should('contain', 'v1.10');
  });
});
