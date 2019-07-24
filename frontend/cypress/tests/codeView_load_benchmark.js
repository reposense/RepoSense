describe('load code view benchmark', function() {
  const NUM_TRIALS = 3;
  const THRESHOLD_LOADING_TIME = 8000;
  const THRESHOLD_LOADING_TIME_SECONDS = THRESHOLD_LOADING_TIME / 1000;

  const BUFFER_PERCENTAGE = 0.1;
  const BUFFER_SUGGESTED_TIME = 3000;
  const ALLOWED_BUFFER_TIME = BUFFER_PERCENTAGE * THRESHOLD_LOADING_TIME >= BUFFER_SUGGESTED_TIME
                              ? BUFFER_PERCENTAGE * THRESHOLD_LOADING_TIME : BUFFER_SUGGESTED_TIME;
  const ALLOWED_BUFFER_TIME_SECONDS = ALLOWED_BUFFER_TIME / 1000;

  const MAXIMUM_LOADING_TIME = THRESHOLD_LOADING_TIME + ALLOWED_BUFFER_TIME;

  let loadingTimes = [];

  const timeTrial = function(i) {
      let startTime;
      // ensure that icons are loaded
      Cypress.wait();

      cy.get('#summary-wrapper .sort-within-group select')
        .select('totalCommits dsc');

      // ---- need the following 2 cy.get(.) while awaiting PR #828
      cy.get('#summary-wrapper .sort-within-group select')
        .select('totalCommits');

      cy.get('#summary-wrapper .sort-within-group select')
        .select('totalCommits dsc');
      // ----- end of to delete

      cy.get('.summary-chart__title--button.fa-code')
        .should('be.visible')
        .first()
        .click()
        .then(() => {
          startTime = performance.now();
        });

      cy.get('#tab-authorship .files')
        .then(() => {
          const endTime = performance.now();
          const loadingTime = endTime - startTime;

          loadingTimes.push(loadingTime);
        });
  };


  for (let i = 0; i < NUM_TRIALS; i++) {
    it(`time taken to load code view (trial ${i+1})`, function() {
      timeTrial(i);
    });
  }

  it(`at least one trial is within ${THRESHOLD_LOADING_TIME_SECONDS}(+${ALLOWED_BUFFER_TIME_SECONDS})s`, function() {
    const totalLoadingTime = loadingTimes.reduce((acc, curr) => acc + curr, 0);
    const averageLoadingTime = totalLoadingTime / NUM_TRIALS;
    const averageLoadingTimeSeconds = averageLoadingTime / 1000;

    const isATrialWithinMaxTime = loadingTimes.map(time => time <= MAXIMUM_LOADING_TIME)
                                              .reduce((acc, curr) => acc || curr, false);

    assert.isTrue(isATrialWithinMaxTime,
      `average loading time: ${averageLoadingTimeSeconds}s`);
  });
});
