package reposense.ConfigParser;

import reposense.exceptions.ParseException;

public abstract class Parser<TOutputType, TInputType> {

    /**
     * Returns the parsed input
     *
     * @param input Generic type TInputType
     * @return Generic type TOutputType
     * @throws ParseException If the given input fails to parse.
     */
    public abstract TOutputType parse(TInputType input) throws ParseException;
}
