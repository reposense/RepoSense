describe('scroll button', () => {
    it('button is not shown when only scroll 100 pixels', () => {
        cy.get('#summary-wrapper').scrollTo(0, 100);
        cy.get('#go-back-button').should('not.be.visible');
    });

    it('shows the button when scroll more than 200 pixels', () => {
        cy.get('#summary-wrapper').scrollTo(0, 300);
        cy.get('#go-back-button').should('be.visible');
    });

    it('button scrolls back to top when clicked', () => {
        cy.get('#summary-wrapper').scrollTo(0, 300);
        cy.get('#go-back-button').should('be.visible');
        cy.get('#go-back-button').click();
        cy.get('#summary-wrapper').invoke('scrollTop').should('equal', 0);
    });
})