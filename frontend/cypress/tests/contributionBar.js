describe('contribution bar', () => {
  it('same length when breakdown selected', () => {
    let expectedWidthSum = 0;
    cy.get('.summary-chart__contrib--bar').then((ele) => {
      let i;
      for (i = 0; i < ele.length; i += 1) {
        expectedWidthSum += parseFloat(ele[i].style.width.split('%')[0]);
      }
    });

    cy.get('#summary-wrapper label').contains('breakdown by file type').siblings().filter('input')
        .check();

    Cypress.wait();

    let actualWidthSum = 0;
    cy.get('.summary-chart__contrib--bar').then((ele) => {
      let i;
      for (i = 0; i < ele.length; i += 1) {
        actualWidthSum += parseFloat(ele[i].style.width.split('%')[0]);
      }
    });
    expect(actualWidthSum.toFixed(3)).to.be.equal(expectedWidthSum.toFixed(3));
  });

  it('no contribution bar when breakdown selected and all file types unchecked', () => {
    cy.get('#summary-wrapper label').contains('breakdown by file type').siblings().filter('input')
        .check();

    cy.get('#summary-wrapper label').contains('All').siblings().filter('input')
        .uncheck();

    Cypress.wait();

    cy.get('.summary-chart__contrib--bar').should('not.visible');
  });

  it('display selected file types only', () => {
    let expectedWidthSum = 0;
    cy.get('#summary-wrapper label').contains('breakdown by file type').siblings().filter('input')
        .check();
    cy.get('.summary-chart__contrib--bar').then((ele) => {
      expectedWidthSum += parseFloat(ele[0].style.width.split('%')[0]);
    });

    cy.get('#summary-wrapper label').contains('All').siblings().filter('input')
        .uncheck();

    cy.get('#summary-wrapper label').contains('gradle').siblings().filter('input')
        .check();

    Cypress.wait();

    let actualWidthSum = 0;
    cy.get('.summary-chart__contrib--bar').then((ele) => {
      let i;
      for (i = 0; i < ele.length; i += 1) {
        actualWidthSum += parseFloat(ele[i].style.width.split('%')[0]);
      }
    });
    expect(actualWidthSum.toFixed(3)).to.be.equal(expectedWidthSum.toFixed(3));
  });
});
