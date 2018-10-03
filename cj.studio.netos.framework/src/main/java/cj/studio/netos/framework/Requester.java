package cj.studio.netos.framework;

public class Requester implements IRequester {
    IAxon input;
    public  Requester(IAxon input){
        this.input=input;
    }
    @Override
    public void request(Frame frame){
        input.nextFlow(frame,null);
    }
}
