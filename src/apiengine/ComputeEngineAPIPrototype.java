package apiengine;

import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import project.annotations.ConceptualAPIPrototype;

public class ComputeEngineAPIPrototype  {

    @ConceptualAPIPrototype
    public ComputationOutput runComputation(ComputationInput input) {
        return new ComputationOutput("2 × 2 × 3 × 5");
    }
}

//package datastore;
//
//
//public class DataStorePrototype {
//
//    public void prototype(DataStore store) {
//        // Specifically: Store the string "Hello world"
//        
//        // store bytes
//        
//        DataStoreResponse hwKey = store.insertRequest(new DataRequest("Hello world".getBytes()));
//        DataStoreResponse classKey = store.insertRequest(new DataRequest("CPS 353".getBytes()));
//        
//        if (hwKey.getResultCode().success()) {
//            //        Later, get back the string that we stored (Hello world)
//            String result = new String(store.loadData(hwKey.getId()));
//        }
//
//    }
//}
