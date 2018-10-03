package cj.studio.netos.x;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IAxon;
import cj.studio.netos.framework.ICellsap;
import cj.studio.netos.framework.INeuron;
import cj.studio.netos.framework.IReciever;
import cj.studio.netos.framework.IRecieverCallback;
import cj.studio.netos.framework.IRequester;
import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.ISynapsis;
import cj.studio.netos.framework.annotation.Reciever;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.Viewport;

@Viewport(name = "/splash")
public class SplashScreen extends AppCompatActivity {
    @ServiceSite
    IServiceProvider site;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        INeuron neuron=site.getService("$");
        final IRequester requester=site.getService("$.requester");

//        neuron.cellsap().empty();
        if(neuron.cellsap().checkIdentityIsEmpty()){//到登录界面
            Frame frame=new Frame("navigate /login netos/1.0");
            requester.request(frame);
            finish();//结束之后则可使回退键不到splash界面
        }else{//进入系统到桌面
            ICellsap cellsap=neuron.cellsap();
            neuron.startNetosServiceUseLocal();
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    Frame frame=new Frame("navigate / netos/1.0");
                    frame.head("test","232323");
                    frame.parameter("zz","....");
                    requester.request(frame);
                    finish();//结束之后则可使回退键不到splash界面
                }
            },1000);
        }
        neuron.inputAxon().add(new ISynapsis() {
            @Override
            public void flow(Frame frame, IAxon axon) {
                if("/handshake".equals(frame.path())){
                    Log.i("HandshakeViewport",frame.toString());
                    return;
                }
                if("/disconnect".equals(frame.path())){
                    Log.i("HandshakeViewport",frame.toString());
                    return;
                }
                axon.nextFlow(frame,this);
            }

            @Override
            public void dispose() {

            }
        });
    }
}
