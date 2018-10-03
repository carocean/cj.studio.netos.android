package cj.studio.netos.framework;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cj.studio.netos.framework.net.IConnection;
import cj.studio.netos.framework.net.MessageLooperOutput;
import cj.studio.netos.framework.net.TcpConnection;
import cj.studio.netos.framework.service.NetosReaderService;
import cj.studio.netos.framework.service.NetosServiceConnection;
import cj.studio.netos.framework.service.NetosWriterService;
import cj.studio.netos.framework.synapsis.FirstExternalSynapsis;
import cj.studio.netos.framework.synapsis.FirstInternalSynapsis;
import cj.studio.netos.framework.synapsis.LastExternalSynapsis;
import cj.studio.netos.framework.synapsis.LastInternalSynapsis;

public class Neuron extends Application implements INeuron, IServiceProvider {
    IAxon inputAxon;//外部的轴突，输入神经元，内部组件不可见
    IAxon outputAxon;//本神经元的轴突，本神经元的输出,通过cell作用服务提供给内部组件使用
    IServiceProvider sysprovider;
    IRequester requester;
    ICell cell;
    ICellsap cellsap;
    ServiceConnection readerServiceConnection;
    IConnection connection;
    NetosServiceConnection writerServiceConnection;

    @Override
    public final IAxon inputAxon() {
        return inputAxon;
    }

    @Override
    public ICellsap cellsap() {
        return cellsap;
    }

    @Override
    public final IAxon outputAxon() {
        return outputAxon;
    }

    @Override
    public ICell cell() {
        return cell;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refresh();
    }

    @Override
    public void onTerminate() {
        inputAxon.dispose();
        outputAxon.dispose();
        super.onTerminate();
    }

    @Override
    public void refresh() {
        cellsap = new Cellsap(this);
        cell = new Workbench(this);
        cell.refresh();

        connection = new TcpConnection();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            inputAxon = new ExternalAxon(new FirstExternalSynapsis(), new LastExternalSynapsis(this, packageInfo.activities));
            outputAxon = new InternalAxon(new FirstInternalSynapsis(), new LastInternalSynapsis(connection));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        requester = new Requester(inputAxon);

    }

    @Override
    public void startNetosService(LoginFrom loginFrom) {
        cellsap.token(loginFrom.token);
        cellsap.principal(loginFrom.principal);
        cellsap.remoteAddressList(loginFrom.addressList);
        cellsap.flush();

        startNetosServiceUseLocal();
    }

    @Override
    public void startNetosServiceUseLocal() {
        Intent readerintent = new Intent(this, NetosReaderService.class);
        readerintent.putExtra("addresslist", cellsap.remoteAddressList());
        readerintent.putExtra("domain", String.format("tcp://%s.com", cellsap.principal()));
        readerintent.putExtra("token", cellsap.token());
        MessageLooperOutput looperOutput = new MessageLooperOutput(inputAxon);
        readerServiceConnection = new NetosServiceConnection(readerintent, this, looperOutput, connection);
        bindService(readerintent, readerServiceConnection, Context.BIND_AUTO_CREATE);//由于在NetosServiceConnection中实现了服务的启停，所以调用者仅需要绑定了解绑

        Intent writerintent = new Intent(this, NetosWriterService.class);
        writerintent.putExtra("addresslist", cellsap.remoteAddressList());
        writerintent.putExtra("domain", String.format("tcp://%s.com", cellsap.principal()));
        writerintent.putExtra("token", cellsap.token());
        writerServiceConnection = new NetosServiceConnection(writerintent, this, looperOutput, connection);
        bindService(writerintent, writerServiceConnection, Context.BIND_AUTO_CREATE);//由于在NetosServiceConnection中实现了服务的启停，所以调用者仅需要绑定了解绑

    }


    @Override
    public void unstartNetosService() {
        unbindService(readerServiceConnection);
        unbindService(writerServiceConnection);
    }

    @Override
    public boolean isNetosServiceRunning() {//由于netosservice管理着connection的启停，因此，它的状态就是服务的状态
        return connection.isConnected();
    }

    @Override
    public <T> T getService(String name) {
        if (name.equals("$")) {
            return (T) this;
        }
        if (name.equals("$.workbench") || "cell".equals(name)) {
            return (T) cell;
        }
        if ("$.cellsap".equals(name)) {
            return (T) cellsap;
        }
        if (name.equals("$.axon.output")) {
            return (T) outputAxon;
        }
        if (name.equals("$.axon.input")) {
            return (T) inputAxon;
        }
        if (name.equals("$.requester")) {
            return (T) requester;
        }
        return sysprovider.getService(name);
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        if (INeuron.class.isAssignableFrom(clazz) || Application.class.isAssignableFrom(clazz)) {
            return (T) this;
        }
        if (IRequester.class.isAssignableFrom(clazz)) {
            return (T) requester;
        }
        if (ICell.class.isAssignableFrom(clazz) || IWorkbench.class.isAssignableFrom(clazz)) {
            return (T) cell;
        }
        return null;
    }
}
