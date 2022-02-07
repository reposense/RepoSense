// Disable screenshots

// https://docs.cypress.io/api/cypress-api/screenshot-api.html#Disable-screenshots-on-run-failures
Cypress.Screenshot.defaults({
  screenshotOnRunFailure: false,
});

beforeEach(() => {
  cy.visit('/');
});
