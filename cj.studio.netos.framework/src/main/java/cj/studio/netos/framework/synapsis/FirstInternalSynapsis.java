package cj.studio.netos.framework.synapsis;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IAxon;
import cj.studio.netos.framework.ISynapsis;

public class FirstInternalSynapsis implements ISynapsis{
    @Override
    public void flow(Frame frame, IAxon axon) {
        axon.nextFlow(frame,this);
    }

    @Override
    public void dispose() {

    }
}
