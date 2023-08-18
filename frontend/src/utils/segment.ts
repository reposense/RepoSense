export default class Segment {
  knownAuthor: string | null;

  isFullCredit: boolean;

  lineNumbers: Array<number>;

  lines: Array<string>;

  constructor(
    knownAuthor: string | null,
    isFullCredit: boolean,
    lineNumbers: Array<number>,
    lines: Array<string>,
  ) {
    this.knownAuthor = knownAuthor;
    this.isFullCredit = isFullCredit;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
