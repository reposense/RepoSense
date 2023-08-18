export default class Segment {
  knownAuthor: string | null;

  lineNumbers: Array<number>;

  lines: Array<string>;

  isFullCredits: Array<boolean>;

  constructor(
    knownAuthor: string | null,
    lineNumbers: Array<number>,
    lines: Array<string>,
    isFullCredits: Array<boolean>,
  ) {
    this.knownAuthor = knownAuthor;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
    this.isFullCredits = isFullCredits;
  }
}
