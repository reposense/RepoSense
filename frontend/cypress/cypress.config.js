const { defineConfig } = require('cypress')

module.exports = defineConfig({
  fixturesFolder: false,
  video: false,
  defaultCommandTimeout: 30000,
  pageLoadTimeout: 180000,
  e2e: {
    setupNodeEvents(on, config) {},
    baseUrl: 'http://localhost:9000',
    specPattern: 'tests/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'support.js',
  },
})
