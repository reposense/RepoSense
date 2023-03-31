export default class Segment {
  knownAuthor: string | null;

  lineNumbers: Array<number>;

  lines: Array<string>;

  constructor(knownAuthor: string | null, lineNumbers: Array<number>, lines: Array<string>) {
    this.knownAuthor = knownAuthor;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
