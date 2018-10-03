package cj.studio.netos.framework.service;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.NetException;
import cj.studio.netos.framework.net.IConnection;
import cj.studio.netos.framework.net.MessageLooperOutput;

public class NetosServiceConnection implements ServiceConnection {
    private final IConnection connection;
    MessageLooperOutput looperOutput;
    IServiceProvider site;
    NetosBinder binder;
    Intent intent;

    public NetosServiceConnection(Intent intent, IServiceProvider site, MessageLooperOutput looperOutput, IConnection netconnection) {
        this.looperOutput = looperOutput;
        this.site = site;
        this.intent = intent;
        this.connection = netconnection;
    }

    @Override
    public void onBindingDied(ComponentName name) {
        site = null;
        looperOutput = null;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (binder != null) {

            Application application = site.getService("$");
            application.stopService(intent);
        }
        site = null;
        looperOutput = null;
        binder = null;
        intent = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (NetosBinder) service;

        binder.setLooperOutput(looperOutput);


        binder.setConnection(connection);

        Application application = site.getService("$");
        application.startService(intent);//一定要在此启动服务，否则如果在外部启动服务，服务的onHandleIntent方法可能会执行在onServiceConnected后
    }


}