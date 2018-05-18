package reposense.parsers;

import reposense.exception.ParseException;

/**
 * An abstract generic parser class, which can be implemented by concrete parsers.
 */
public abstract class Parser<TOutputType, TInputType> {

    public abstract TOutputType parse(TInputType input) throws ParseException;
}
