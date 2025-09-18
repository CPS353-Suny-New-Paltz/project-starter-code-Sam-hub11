package cs353_api_network;

import java.util.*;

public class ComputationInput 
{
    private final int[] numbers;

    public ComputationInput(int[] numbers) 
    {
        this.numbers = numbers;
    }

    public int[] getNumbers() 
    { 
    	return numbers; 
    }

    @Override
    public String toString() 
    {
        return Arrays.toString(numbers);
    }
}
