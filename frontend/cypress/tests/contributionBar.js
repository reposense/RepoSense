describe('contribution bar', () => {
  it('same length when breakdown selected', () => {
    let expectedSum = 0;
    cy.get('.summary-chart__contrib--bar').then((ele) => {
      let i;
      for (i = 0; i < ele.length; i += 1) {
        expectedSum += parseFloat(ele[i].style.width.split('%')[0]);
      }
    });

    cy.get('#summary-wrapper label').contains('breakdown by file format').siblings().filter('input')
        .check();

    cy.get('.summary-chart__contrib--bar--fileformat').then((ele) => {
      let actualSum = 0;
      let i;
      for (i = 0; i < ele.length; i += 1) {
        actualSum += parseFloat(ele[i].style.width.split('%')[0]);
      }
      expect(actualSum.toFixed(3)).to.be.equal(expectedSum.toFixed(3));
    });
  });
});
