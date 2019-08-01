// Disable screenshots

// https://docs.cypress.io/api/cypress-api/screenshot-api.html#Disable-screenshots-on-run-failures
Cypress.Screenshot.defaults({
  screenshotOnRunFailure: false,
});

beforeEach(() => {
  cy.visit('/');
});

// Slows down test execution on non-CI environment.
Cypress.wait = (() => {
  if (Cypress.env('ci') === true) {
    return;
  }

  cy.log('Slowing down test...');
  cy.wait(1500);
});
