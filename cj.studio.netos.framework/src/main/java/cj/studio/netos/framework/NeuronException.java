package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/1/20.
 */

public class NeuronException extends Exception {
    public NeuronException() {
        super();
    }
    public NeuronException(String message){
        super(message);
    }
    public NeuronException(String message, Throwable e){
        super(message,e);
    }
    public NeuronException(Throwable e){
        super(e);
    }
}
