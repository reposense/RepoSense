export default class Segment {
  knownAuthor: string | null;

  lineNumbers: number[];

  lines: string[];

  constructor(knownAuthor: string | null, lineNumbers: number[], lines: string[]) {
    this.knownAuthor = knownAuthor;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
