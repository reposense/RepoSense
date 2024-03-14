describe('diffstat', () => {
  it('should render container for contribution bars', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message .stacked-bar-container')
      .should('be.visible');
  });

  // Assumptions: The commit selected here is @eugenepeh's
  // `README: Fix grammatical error` with 1 insertion and 1 deletion.
  it('should render non-empty contribution bars for commits with changes', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message')
      .eq(1)
      .within(() => {
        cy.get('.stacked-bar__contrib--bar')
          .then((element) => {
            expect(element.length).to.be.equal(2);
            expect(element[0].style['background-color']).to.be.equal('limegreen');
            expect(element[0].style.width).to.be.equal('0.1%');
            expect(element[1].style['background-color']).to.be.equal('red');
            expect(element[1].style.width).to.be.equal('0.1%');
          });
      });
  });

  // Assumptions: The commit selected here is @eugenepeh's
  // `Merge branch 'new-branch` into cypress` with 0 insertions and 0 deletions.
  it('should render empty contribution bars for commits with no changes', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    cy.get('#tab-zoom .commit-message')
      .first()
      .within(() => {
        cy.get('.stacked-bar__contrib--bar')
          .then((element) => {
            expect(element.length).to.be.equal(2);
            expect(element[0].style['background-color']).to.be.equal('limegreen');
            expect(element[0].style.width).to.be.equal('0%');
            expect(element[1].style['background-color']).to.be.equal('red');
            expect(element[1].style.width).to.be.equal('0%');
          });
      });
  });

  it('should render contribution bars in proportion', () => {
    cy.get('.icon-button.fa-list-ul')
      .should('exist')
      .first()
      .click();

    let insertionWidthSum = 0;
    let deletionWidthSum = 0;
    let widthProportion = 0;
    cy.get('#tab-zoom .commit-message .stacked-bar__contrib--bar')
      .then((element) => {
        for (let i = 0; i < element.length; i += 1) {
          const val = parseFloat(element[i].style.width.split('%')[0]);
          if (element[i].style['background-color'] === 'limegreen') {
            insertionWidthSum += val;
          } else {
            deletionWidthSum += val;
          }
        }
        widthProportion = insertionWidthSum / deletionWidthSum;
      });

    let insertions = 0;
    let deletions = 0;
    let actualProportion = 0;
    cy.get('[data-cy="changes"]')
      .invoke('text')
      .then((text) => {
        const temp = text.split('lines');
        for (let i = 0; i < temp.length - 1; i += 1) {
          insertions += parseFloat(temp[i].split('-')[0].split('+')[1].trim());
          deletions += parseFloat(temp[i].split('-')[1].trim());
        }
        actualProportion = insertions / deletions;
        expect(widthProportion.toFixed(3)).to.be.equal(actualProportion.toFixed(3));
      });
  });
});
