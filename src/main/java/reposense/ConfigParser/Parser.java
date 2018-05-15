package reposense.ConfigParser;

import reposense.exceptions.ParseException;

public abstract class Parser<TOutputType, TInputType> {

    public abstract TOutputType parse(TInputType input) throws ParseException;
}
