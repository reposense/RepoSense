describe("widget mode copy and logo verification", () => {
  it("clipboard button copies iframe tag and iframe source displays logo", () => {
    // Stub the clipboard API to capture copied content
    cy.window().then((win) => {
      cy.stub(win.navigator.clipboard, "writeText").as("clipboardWrite");
    });

    // Click the first widget clipboard button to copy iframe string to clipboard
    cy.get(".fa-clipboard")
      .should('exist')
      .first()
      .click();

    // Verify clipboard content includes the iframe tag
    cy.get("@clipboardWrite").should("have.been.calledOnce");

    // Verify clipboard content includes the iframe tag
    cy.get("@clipboardWrite").then((clipboardCall) => {
      const copiedText = clipboardCall.args[0][0];
      expect(copiedText).to.include("<iframe");
      expect(copiedText).to.include('src="'); // Ensure there's a src attribute

      // Extract the iframe src
      const srcMatch = copiedText.match(/src="([^"]+)"/);
      expect(srcMatch).to.not.be.null;
      const iframeSrc = srcMatch[1];

      // Visit the iframe source
      cy.visit(iframeSrc);

      // Check for logo existence and styles
      cy.get(".logo")
        .should("be.visible")
        .and("have.css", "display", "flex")
        .and("have.css", "justify-content", "flex-end");
    });
  });
});
