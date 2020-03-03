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
});
