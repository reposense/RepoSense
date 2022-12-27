export default class Segment {
  authored: boolean;

  lineNumbers: Array<number>;

  lines: Array<string>;

  constructor(isAuthored: boolean, lineNumbers: Array<number>, lines: Array<string>) {
    this.authored = isAuthored;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
