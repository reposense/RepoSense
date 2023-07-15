describe('zoom features in code view', () => {
  const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';
  it('click on view commits button', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
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

describe('date changes in chart view should reflect in zoom', () => {
  it('changing \'since\' date range changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugene');

    cy.get('input[name="since"]:visible')
      .type('2018-06-11');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2018-06-12] Setup AppVeyor CI (#142): +19 -0 lines ');
  });

  it('changing \'since\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('yong hao');

    cy.get('input[name="since"]:visible')
      .type('2018-05-20');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2018-05-20] Apply single responsibility principle to frontend component (#99): +201 -90 lines ');
  });

  it('changing the \'until\' date changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugene');

    cy.get('input[name="until"]:visible')
      .type('2019-08-19');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2019-08-18] AboutUs: update team members (#867): +94 -12 lines ');
  });

  it('changing the \'until\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('metta');

    cy.get('input[name="until"]:visible')
      .type('2018-07-20');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2018-07-20] Dashboard: remove navigation bar (#171): +5 -1 lines ');
  });

  it('changing the \'until\' and \'since\' date changes the zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('eugene');

    cy.get('input[name="since"]:visible')
      .type('2018-08-27');

    cy.get('input[name="until"]:visible')
      .type('2019-03-09');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2019-03-08] [#587] Fix unoriented output messages (#593): +105 -69 lines ');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2018-08-27] [#311] code view: differentiate untouched code more (#314): +12 -5 lines ');
  });

  it('changing the \'until\' and \'since\' date again results in a different zoom view', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('james');

    cy.get('input[name="since"]:visible')
      .type('2019-07-22');

    cy.get('input[name="until"]:visible')
      .type('2019-08-01');

    cy.get('.icon-button.fa-list-ul')
      .should('be.visible')
      .first()
      .click();

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2019-08-01] Zoom Tab: revamp header ui (#848): +53 -30 lines ');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should(
        'eq',
        '[2019-07-24] [#828] Revert "v_summary.js: remove redundant '
        + 'calls to getFiltered() (#800)" (#832): +0 -9 lines ',
      );
  });
});

describe('range changes in chartview should reflect in zoom', () => {
  const zoomKey = Cypress.platform === 'darwin' ? '{meta}' : '{ctrl}';

  it('selecting the initial righthand and lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('james');
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(120, 20)
      .click(250, 20);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2021-01-04] Update `About us` page (#1393): +55 -30 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2019-12-20] [#46] Show total time after batch processing (#758): +43 -0 lines ');
  });

  it('changing the righthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('james');
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(120, 20)
      .click(200, 20);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2020-10-01]  [#1312] Conditional run for markbind to gh pages deployment (#1337): +1 -0 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2019-12-20] [#46] Show total time after batch processing (#758): +43 -0 lines ');
  });

  it('changing the lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('james');
    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(200, 20)
      .click(250, 20);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should('eq', '[2021-01-04] Update `About us` page (#1393): +55 -30 lines ');
    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2020-11-21] [#1345] Authorship: Add last modified '
      + 'date to each LoC if specified (#1348): +165 -76 lines ');
  });

  it('changing the righthand and lefthand boundary', () => {
    cy.get('div.mui-textfield.search_box > input:visible')
      .should('be.visible')
      .type('james');

    cy.get('body').type(zoomKey, { release: false })
      .get('#summary-charts .summary-chart__ramp .ramp')
      .first()
      .click(200, 20)
      .click(225, 20);

    cy.get('#tab-zoom')
      .should('be.visible');

    cy.get('#tab-zoom .ramp .ramp__slice')
      .first()
      .invoke('attr', 'title')
      .should(
        'eq',
        '[2021-01-04] Update `About us` page (#1393): +55 -30 lines ',
      );

    cy.get('#tab-zoom .ramp .ramp__slice')
      .last()
      .invoke('attr', 'title')
      .should('eq', '[2020-11-21] [#1345] Authorship: Add last modified date '
      + 'to each LoC if specified (#1348): +165 -76 lines ');
  });
});
