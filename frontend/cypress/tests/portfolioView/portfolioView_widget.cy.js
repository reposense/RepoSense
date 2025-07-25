describe('widget',
  {baseUrl: Cypress.env('portfolioBaseUrl')},
  () => {
    beforeEach(() => {
      // Stub the clipboard API to capture copied content
      cy.window().then((win) => {
        cy.stub(win.navigator.clipboard, 'writeText').as('clipboardWrite');
      });

    });

    it('group link should open the correct group\'s widget', () => {
      cy.get('.summary-charts__title .fa-clipboard')
        .first()
        .click()

      // Verify clipboard content includes the iframe tag
      cy.get('@clipboardWrite').should('have.been.called');

      // Verify clipboard content includes the iframe tag
      cy.get('@clipboardWrite').then((clipboardCall) => {
        const copiedText = clipboardCall.args[0][0];
        expect(copiedText).to.include('<iframe');
        expect(copiedText).to.include('src="');

        // Extract and visit the iframe src
        const srcMatch = copiedText.match(/src="([^"]+)"/);
        expect(srcMatch).to.not.be.null;
        const iframeSrc = srcMatch[1];
        cy.visit(iframeSrc);

        cy.get('.summary-charts__title--groupname')
          .should('have.length', 1)
          .contains('reposense/publish-RepoSense[master]');

        cy.get('.summary-chart')
          .should('have.length', 1);
      });
    });

    it('user link should open the correct user\'s widget', () => {
      cy.get('.summary-charts')
        .contains('.summary-charts__title--groupname', 'reposense/repoSense-action[main]')
        .parents('.summary-charts')
        .find('.summary-chart__title .fa-clipboard')
        .first()
        .click()

      // Verify clipboard content includes the iframe tag
      cy.get('@clipboardWrite').should('have.been.calledOnce');

      // Verify clipboard content includes the iframe tag
      cy.get('@clipboardWrite').then((clipboardCall) => {
        const copiedText = clipboardCall.args[0][0];
        expect(copiedText).to.include('<iframe');
        expect(copiedText).to.include('src="');

        // Extract and visit the iframe src
        const srcMatch = copiedText.match(/src="([^"]+)"/);
        expect(srcMatch).to.not.be.null;
        const iframeSrc = srcMatch[1];
        cy.visit(iframeSrc);

        cy.get('.summary-charts__title--groupname')
          .should('have.length', 1)
          .contains('reposense/repoSense-action[main]');

        cy.get('.summary-chart')
          .should('have.length', 1);
      });
    });
  },
);
