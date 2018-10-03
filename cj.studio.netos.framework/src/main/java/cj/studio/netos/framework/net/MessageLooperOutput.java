package cj.studio.netos.framework.net;

import android.os.Handler;
import android.os.Message;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IAxon;

public class MessageLooperOutput extends Handler {
    private final IAxon inputAxon;

    public MessageLooperOutput(IAxon inputAxon) {
        this.inputAxon=inputAxon;
    }

    @Override
    public void handleMessage(Message msg) {
        String framejosn=msg.getData().getString("frame-json");
        Frame frame=Frame.createFrame(framejosn,Frame.class);
        inputAxon.headFlow(frame);
    }
}
