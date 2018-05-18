package reposense.parsers;

import reposense.exception.ParseException;

/**
 * An generic parser interface, which can be implemented by concrete parsers.
 */
public interface Parser<TOutputType, TInputType> {

    TOutputType parse(TInputType input) throws ParseException;
}
