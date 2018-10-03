package cj.studio.netos.framework.net;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.ICloseable;
import cj.studio.netos.framework.NetException;

/**
 * Created by caroceanjofers on 2018/1/19.
 */

public interface IConnection extends ICloseable {

    boolean isConnected();

    void connect(String domain, String token, String address) throws NetException;

    void tryconnect(String domain, String token, String[] addressarr) throws NetException;

    void disconnect();

    void loopRead(MessageLooperOutput output) throws NetException;

    void send(Frame frame) throws NetException;

    void loopWrite() throws NetException;
}
