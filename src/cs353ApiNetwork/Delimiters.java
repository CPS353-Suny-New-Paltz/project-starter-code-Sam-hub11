package cs353ApiNetwork;

public class Delimiters 
{
    private final String pairDelimiter;
    private final String resultDelimiter;

    public Delimiters(String pairDelimiter, String resultDelimiter) 
    {
        this.pairDelimiter = pairDelimiter;
        this.resultDelimiter = resultDelimiter;
    }

    public String getPairDelimiter() 
    {
    	return pairDelimiter; 
    }
    public String getResultDelimiter() 
    {
    	return resultDelimiter; 
    }
}

