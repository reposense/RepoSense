package reposense.ConfigParser;

import reposense.exceptions.ParseException;

public abstract class Parser<TOutputType, TInputType> {
    private static final int PARSE_EXCEPTION_DUMMY_LOCATION = 0;

    public abstract TOutputType parse(TInputType input) throws ParseException;
}
