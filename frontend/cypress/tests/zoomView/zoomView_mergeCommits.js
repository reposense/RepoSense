describe('merge commits', () => {
    it('show merge commits when all file types selected', () => {
      // open the commit panel
      cy.get('.icon-button.fa-list-ul')
        .should('be.visible')
        .first()
        .click();
    });
});
