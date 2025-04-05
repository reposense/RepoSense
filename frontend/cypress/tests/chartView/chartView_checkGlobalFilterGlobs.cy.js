describe('Global filter globs display test', () => {
  it('should display tooltip message on hover and hide it on mouseout', () => {
    cy.get('.mui-textfield.filter_file .tooltip').trigger('mouseover')

    cy.get('span.tooltip-text').should('be.visible').and('contain', 'Type a glob keyword to filter the list')

    cy.get('.mui-textfield.filter_file .tooltip').trigger('mouseout')

    cy.get('span.tooltip-text').should('not.be.visible')
  })
})