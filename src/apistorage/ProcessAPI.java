package apistorage;

import java.util.List;

@project.annotations.ProcessAPI
public interface ProcessAPI {
	//reading input
	List<String> readInput();
	
    boolean writeOutput(String data);
}
