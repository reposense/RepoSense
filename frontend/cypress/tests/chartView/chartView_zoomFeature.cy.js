const extractDate = (str) => {
  const regex = /\[(.*?)\]/g;
  const matches = str.match(regex);
  return new Date(matches[0].slice(1, -1));
};

describe('zoom features in code view', () => {
  const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';
  it('click on view commits button', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');
  });

  it('zoom into ramp', () => {
    // clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(10, 20)
      .click(50, 20);

    cy.get('#tab-zoom')
      .should('be.visible');
  });

  it('zoom into ramp when merge group', () => {
    cy.get('#summary label.merge-group > input:visible')
      .should('be.visible')
      .check()
      .should('be.checked');

    // clicking from the 10th px to the 50th px in the ramp
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(10, 20)
      .click(50, 20);

    cy.get('#tab-zoom')
      .should('be.visible');
  });
});

// Assumptions: Contributer 'eugenepeh' is the first result.
describe('date changes in chart view should reflect in zoom', () => {
  it('setting \'since\' date range changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugenepeh');

    cy.get('input[name="since"]:visible')
      .type('2018-06-11T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.gte', new Date('2018-06-11T00:00:00'));
          });
      });
  });
  // Assumptions: Contributer 'yong24s' is the first result.
  it('setting \'since\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('yong24s');

    cy.get('input[name="since"]:visible')
      .type('2018-05-20T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.gte', new Date('2018-05-20T00:00:00'));
          });
      });
  });

  // Assumptions: Contributer 'eugenepeh' is the first result.
  it('setting the \'until\' date changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugenepeh');

    cy.get('input[name="until"]:visible')
      .type('2019-08-19T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.lte', new Date('2019-08-19T00:00:00'));
          });
      });
  });

  // Assumptions: Contributer 'ongspxm' is the first result.
  it('setting the \'until\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('ongspxm');

    cy.get('input[name="until"]:visible')
      .type('2018-07-20T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.lte', new Date('2018-07-20T00:00:00'));
          });
      });
  });

  // Assumptions: Contributer 'eugenepeh' is the first result.
  it('setting the \'until\' and \'since\' date changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugenepeh');

    cy.get('input[name="since"]:visible')
      .type('2018-08-27T00:00:00');

    cy.get('input[name="until"]:visible')
      .type('2019-03-09T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.lte', new Date('2019-03-09T00:00:00'));
          });
      });

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.gte', new Date('2018-08-27T00:00:00'));
          });
      });
  });

  // Assumptions: Contributer 'jamessspanggg' is the first result.
  it('setting the \'until\' and \'since\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('jamessspanggg');

    cy.get('input[name="since"]:visible')
      .type('2019-07-22T00:00:00');

    cy.get('input[name="until"]:visible')
      .type('2019-08-01T00:00:00');

    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.lte', new Date('2019-08-01T00:00:00'));
          });
      });

    cy.get('#tab-zoom .ramp .ramp__slice')
      .each(($element) => {
        cy.wrap($element)
          .invoke('attr', 'title')
          .then((titleStr) => {
            const date = extractDate(titleStr);
            cy.wrap(date).should('be.gte', new Date('2019-07-22T00:00:00'));
          });
      });
  });
});

describe('range changes in chartview should reflect in zoom', () => {
  const zoomKeyOption = Cypress.platform === 'darwin' ? { metaKey: true } : { ctrlKey: true };

  // Assumptions: Contributer 'jamessspanggg' is the first result,
  // he does not add more commits in the future,
  // and the sort feature is working as expected.
  it('selecting the initial righthand and lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('jamessspanggg');
    cy.get('input[name="until"]:visible')
      .type('2023-12-31T00:00:00');

    cy.get('body')
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(120, 20, zoomKeyOption)
      .click(250, 20, zoomKeyOption);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2021-01-04T00:00:00] Update `About us` page (#1393): +55 -30 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2019-12-20T00:00:00] [#46] Show total time after batch processing (#758): +43 -0 lines ');
  });

  // Assumptions: Contributer 'jamessspanggg' is the first result,
  // he does not add more commits in the future,
  // and the sort feature is working as expected.
  it('changing the righthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('jamessspanggg');
    cy.get('input[name="until"]:visible')
      .type('2023-12-31T00:00:00');

    cy.get('body')
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(120, 20, zoomKeyOption)
      .click(170, 20, zoomKeyOption);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2020-05-23T00:00:00] [#1241] Restore checked file types (#1256): +14 -1 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2019-12-20T00:00:00] [#46] Show total time after batch processing (#758): +43 -0 lines ');
  });

  // Assumptions: Contributer 'jamessspanggg' is the first result,
  // he does not add more commits in the future,
  // and the sort feature is working as expected.
  it('changing the lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('jamessspanggg');
    cy.get('input[name="until"]:visible')
      .type('2023-12-31T00:00:00');

    cy.get('body')
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(170, 20, zoomKeyOption)
      .click(250, 20, zoomKeyOption);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2021-01-04T00:00:00] Update `About us` page (#1393): +55 -30 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2020-09-27T00:00:00] Add optional check for quotes in diff file regex (#1330): +1 -1 lines ');
  });

  // Assumptions: Contributer 'jamessspanggg' is the first result,
  // he does not add more commits in the future,
  // and the sort feature is working as expected.
  it('changing the righthand and lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('jamessspanggg');
    cy.get('input[name="until"]:visible')
      .type('2023-12-31T00:00:00');

    cy.get('body')
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(170, 20, zoomKeyOption)
      .click(225, 20, zoomKeyOption);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should(
        'eq',
        '[2021-01-04T00:00:00] Update `About us` page (#1393): +55 -30 lines ',
      );

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2020-09-27T00:00:00] Add optional check for quotes in diff file regex (#1330): +1 -1 lines ');
  });
});
