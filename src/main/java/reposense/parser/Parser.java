package reposense.parser;

import reposense.exception.ParseException;

/**
 * An generic parser interface, which can be implemented by concrete parser.
 */
public interface Parser<TOutputType, TInputType> {

    TOutputType parse(TInputType input) throws ParseException;
}
