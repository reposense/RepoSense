export default class Segment {
  knownAuthor: string | null;

  isFullCredits: boolean;

  lineNumbers: Array<number>;

  lines: Array<string>;

  constructor(
    knownAuthor: string | null,
    isFullCredits: boolean,
    lineNumbers: Array<number>,
    lines: Array<string>,
  ) {
    this.knownAuthor = knownAuthor;
    this.isFullCredits = isFullCredits;
    this.lineNumbers = lineNumbers;
    this.lines = lines;
  }
}
