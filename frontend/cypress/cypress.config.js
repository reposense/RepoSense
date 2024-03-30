const { defineConfig } = require('cypress')

module.exports = defineConfig({
  fixturesFolder: false,
  video: false,
  defaultCommandTimeout: 30000,
  pageLoadTimeout: 400000,
  e2e: {
    setupNodeEvents(on, config) {},
    baseUrl: 'http://localhost:9000',
    chromeWebSecurity: false,
    specPattern: 'tests/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'support.js',
  },
})
