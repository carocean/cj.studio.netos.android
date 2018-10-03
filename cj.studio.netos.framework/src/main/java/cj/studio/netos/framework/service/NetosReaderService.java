package cj.studio.netos.framework.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cj.studio.netos.framework.net.IConnection;
import cj.studio.netos.framework.net.MessageLooperOutput;

public class NetosReaderService extends IntentService {
    NetosBinder binder;

    public NetosReaderService() {
        super("cj.studio.netos.x.reader");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binder = new NetosBinder();

        return binder;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        IConnection connection = binder.getConnection();
        MessageLooperOutput output = binder.getLooperOutput();
        String domain = intent.getStringExtra("domain");
        String token = intent.getStringExtra("token");
        String[] addresslist = intent.getStringArrayExtra("addresslist");
        if (!connection.isConnected()) {
            try {
                connection.tryconnect(domain, token, addresslist);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            connection.loopRead(output);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean stopService(Intent name) {
        if (binder.getConnection().isConnected()) {
            binder.getConnection().disconnect();
        }
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        binder=null;
        super.onDestroy();
    }
}
