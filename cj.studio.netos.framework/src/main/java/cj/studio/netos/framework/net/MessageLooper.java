package cj.studio.netos.framework.net;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.util.NumberUtil;


/**
 * Created by caroceanjofers on 2018/1/23.
 */

public class MessageLooper implements IMessageLooper {
    MessageLooperOutput output;
    int headindex;
    int bodyindex;
    int bodylength;
    byte[] header = new byte[4];
    byte[] body;
    public MessageLooper(MessageLooperOutput output){
        this.output = output;
    }
    @Override
    public void reset() {
        headindex = 0;
        bodyindex = 0;
        bodylength=0;
    }
    @Override
    public void accept(byte b) {
        if (headindex < 4) {
            acceptHead(b);
        } else {
            acceptBody(b);
        }
    }

    private void acceptHead(byte b) {
        header[headindex] = b;
        if (headindex == 3) {
            bodylength = NumberUtil.byte4ToInt(header, 0);
            try {
                body = new byte[bodylength];
            } catch (Exception e) {
                reset();
                Log.e("net","侦已重置.");
                return;
            }
            // 下面让headindex多加一个字节，这样让accept跳到body
        }
        headindex++;
    }

    private void acceptBody(byte b) {
        body[bodyindex] = b;
        if (bodyindex == bodylength - 1) {
            Frame f = new Frame(body);
            try {
                onMessage(f);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                reset();
            }
            return;
        }
        bodyindex++;
    }
    @Override
    public void onMessage(Frame frame) {
        Message message=new Message();
        Bundle bundle=new Bundle();
        bundle.putString("frame-json",frame.toJson());
        message.setData(bundle);
        output.sendMessage(message);
    }
}
