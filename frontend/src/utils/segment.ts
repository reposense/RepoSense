export default class Segment {
  knownAuthor?: string;

  lineNumbers: Array<number>;

  lines: Array<string>;

  constructor(knownAuthor: string, lineNumbers: Array<number>, lines: Array<string>) {
    this.knownAuthor = knownAuthor;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
