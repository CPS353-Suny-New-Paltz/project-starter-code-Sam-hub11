package apiengine;
import project.annotations.ConceptualAPIPrototype;

public class ConAPIPrototype {

    @ConceptualAPIPrototype
    public void demo(ConAPI api) {   
        api.runComputation(5);
    }
}
