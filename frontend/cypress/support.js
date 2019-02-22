// Disable screenshots

// https://docs.cypress.io/api/cypress-api/screenshot-api.html#Disable-screenshots-on-run-failures
Cypress.Screenshot.defaults({
  screenshotOnRunFailure: false,
});

beforeEach(() => {
  cy.visit('/');
});

Cypress.slowMotion = (() => {
  if (Cypress.env('local') === true) {
  	cy.log('Slowing down test...')
  	cy.wait(1250);
  }
});
