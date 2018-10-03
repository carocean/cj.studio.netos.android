package cj.studio.netos.framework.net;


import cj.studio.netos.framework.Frame;

/**
 * Created by caroceanjofers on 2018/1/23.
 */

public interface IMessageLooper {
    void reset();

    void accept(byte b);


    void onMessage(Frame frame);
}
