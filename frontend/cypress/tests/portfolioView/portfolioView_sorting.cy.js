describe('sorting',
  { baseUrl: Cypress.env('portfolioBaseUrl') },
  () => {
    beforeEach(() => {
      cy.readFile('configs/portfolio/repo-config.csv').then((csvString) => {
        const lines = csvString.trim().split('\n');
        // map last 3 repos in repo-config.csv to corresponding display title in summary charts
        const repoNames = lines.slice(-3).map((line) => {
          const values = line.split(',');

          const match = values[0].match(/^https?:\/\/(www\.)?github.com\/([^/]+)\/([^/.]+)(\.git)?/i)
          const repoName = `${match[2]}/${match[3]}`;

          return `${repoName}[${values[1]}]`;
        });
        cy.wrap(repoNames).as('repoNames')
      });
    })

    it('repo order should follow order of rows in repo-config.csv', () => {
      cy.get('.summary-charts__title--groupname')
        .then(($els) => {
          // last 3 summary chart title
          const repoTitles = Cypress._.map($els, (el) => el.textContent.trim()).slice(-3);

          cy.get('@repoNames').then((repoNames) => {
            repoNames.forEach((expectedRepoName, index) => {
              expect(repoTitles[index]).to.eq(expectedRepoName);
            });
          });
        })
    });
  },
);
