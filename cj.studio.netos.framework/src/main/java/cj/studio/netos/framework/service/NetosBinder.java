package cj.studio.netos.framework.service;

import android.os.Binder;

import cj.studio.netos.framework.net.IConnection;
import cj.studio.netos.framework.net.MessageLooperOutput;

class NetosBinder extends Binder {
     private IConnection connection;
     MessageLooperOutput looperOutput;
     public void setConnection(IConnection connection) {
         this.connection=connection;
     }

     public IConnection getConnection() {
         return connection;
     }

     public MessageLooperOutput getLooperOutput() {
        return looperOutput;
     }

     public void setLooperOutput(MessageLooperOutput looperOutput) {
         this.looperOutput = looperOutput;
     }
 }
