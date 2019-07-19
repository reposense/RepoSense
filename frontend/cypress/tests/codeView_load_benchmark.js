describe('load code view benchmark', function() {
  const MAXIMUM_LOADING_TIME = 8000;
  const MAXIMUM_LOADING_TIME_SECONDS = MAXIMUM_LOADING_TIME / 1000;
  let loadingTimeSeconds;

  it(`time taken to load code view is within ${MAXIMUM_LOADING_TIME_SECONDS}s`,
    function() {
    // ensure that icons are loaded
    Cypress.wait();

    let startTime;

    cy.get('.summary-chart__title--name').contains('eugenepeh')
      .parent()
      .within(($title) => {
          cy.get('a .summary-chart__title--button.fa-code')
          .should('be.visible')
          .click();
          startTime = performance.now();
      })

    cy.get('#tab-authorship .files')
      .then(() => {
        const endTime = performance.now();
        const loadingTime = endTime - startTime;

        loadingTimeSeconds = (loadingTime / 1000).toFixed(3);

        assert.isTrue(loadingTime < MAXIMUM_LOADING_TIME,
          "loading time is within limit");
        cy.log(`time taken to load code view: ${loadingTimeSeconds}s`)
      });
  });
});
