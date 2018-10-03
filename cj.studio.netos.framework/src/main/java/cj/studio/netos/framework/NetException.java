package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/1/19.
 */

public class NetException extends NeuronException {
    public NetException() {
        super();
    }
    public NetException(String message){
        super(message);
    }
    public NetException(String message, Throwable e){
        super(message,e);
    }
    public NetException(Throwable e){
        super(e);
    }
}
