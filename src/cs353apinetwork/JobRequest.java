package cs353apinetwork;

public class JobRequest 
{
    private final Input source;
    private final Output destination;
    private final Delimiters delimiters;

    public JobRequest(Input source, Output destination, Delimiters delimiters) 
    {
        this.source = source;
        this.destination = destination;
        this.delimiters = delimiters;
    }

    public Input getSource() 
    { 
    	return source; 
    }
    public Output getDestination() 
    { 
    	return destination; 
    }
    public Delimiters getDelimiters() 
    {
    	return delimiters; 
    }
}
