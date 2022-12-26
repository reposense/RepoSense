export default class Segment {
  authored;

  lineNumbers;

  lines;

  constructor(isAuthored, lineNumbers, lines) {
    this.authored = isAuthored;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
