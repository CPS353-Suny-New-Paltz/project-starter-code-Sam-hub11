package apistorage;
import project.annotations.ProcessAPIPrototype;

public class ProcesAPIPrototype {

    @ProcessAPIPrototype
    public void demo(ProcesAPI api) {
        api.writeOutput("sample");
    }
}
