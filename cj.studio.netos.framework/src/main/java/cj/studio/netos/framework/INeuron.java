package cj.studio.netos.framework;

import cj.studio.netos.framework.net.IConnection;

public interface INeuron {
    IAxon inputAxon();

    IAxon outputAxon();
    ICell cell();
    ICellsap cellsap();
    void refresh();
    void startNetosServiceUseLocal();
    void startNetosService(LoginFrom loginFrom);
    void unstartNetosService();
    public boolean isNetosServiceRunning();
}
