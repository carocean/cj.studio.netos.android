package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/1/19.
 */

public interface ISynapsis extends  IDisposable {
    void flow(Frame frame, IAxon axon);
}
