package cj.studio.netos.framework.synapsis;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IAxon;
import cj.studio.netos.framework.ISynapsis;
import cj.studio.netos.framework.NetException;
import cj.studio.netos.framework.net.IConnection;

public class LastInternalSynapsis implements ISynapsis{
    IConnection connection;
    public LastInternalSynapsis(IConnection connection){
        this.connection=connection;
    }
    @Override
    public void flow(Frame frame, IAxon axon) {
        if(connection==null){
            return;
        }
        try {
            connection.send(frame);
        } catch (NetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {

    }
}
