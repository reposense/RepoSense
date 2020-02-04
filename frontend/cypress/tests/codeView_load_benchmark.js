describe('load code view benchmark', function() {
  const NUM_TRIALS = 5;
  const THRESHOLD_LOADING_TIME = 8000;
  const THRESHOLD_LOADING_TIME_SECONDS = THRESHOLD_LOADING_TIME / 1000;

  const BUFFER_PERCENTAGE = 0.1;
  const BUFFER_SUGGESTED_TIME = 3000;
  const ALLOWED_BUFFER_TIME = BUFFER_PERCENTAGE * THRESHOLD_LOADING_TIME >= BUFFER_SUGGESTED_TIME
                              ? BUFFER_PERCENTAGE * THRESHOLD_LOADING_TIME : BUFFER_SUGGESTED_TIME;
  const ALLOWED_BUFFER_TIME_SECONDS = ALLOWED_BUFFER_TIME / 1000;

  const MAXIMUM_LOADING_TIME = THRESHOLD_LOADING_TIME + ALLOWED_BUFFER_TIME;

  let isATrialWithinMaxTime = false;

  const timeTrial = function(i) {
      let startTime;

      // ensure that icons are loaded
      Cypress.wait();

      cy.get('#summary-wrapper .sort-within-group select')
        .select('totalCommits dsc');

      cy.get('.icon-button.fa-code')
        .should('be.visible')
        .first()
        .click()
        .then(() => {
          startTime = performance.now();
        });

      cy.get('#tab-authorship .files', { timeout: 90000 })
        .should('be.visible')
        .then(() => {
          const endTime = performance.now();
          const loadingTime = endTime - startTime;
          const loadingTimeSeconds = loadingTime / 1000;

          cy.log(`trial ${i+1} loading time: ${loadingTimeSeconds.toFixed(3)}s`);

          if (loadingTime <= MAXIMUM_LOADING_TIME) {
            isATrialWithinMaxTime = true;
          }
        });
  };


  for (let i = 0; i < NUM_TRIALS; i++) {
    it(`time taken to load code view (trial ${i+1})`, function() {
      if (isATrialWithinMaxTime) {
        this.skip();
      }
      timeTrial(i);
    });
  }

  it(`at least one trial is within ${THRESHOLD_LOADING_TIME_SECONDS}(+${ALLOWED_BUFFER_TIME_SECONDS})s`, function() {
    assert.isTrue(isATrialWithinMaxTime);
  });
});
