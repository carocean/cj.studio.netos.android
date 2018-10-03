package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/1/19.
 */

public interface IAxon extends IDisposable{
    void add(ISynapsis synapsis);
    void remove(ISynapsis synapsis);
    void nextFlow(Frame frame, ISynapsis formthis);
    void headFlow(Frame frame);
}
