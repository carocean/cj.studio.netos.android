package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/2/3.
 */

public interface IServiceSite extends IServiceProvider,IDisposable {
    void addService(String name, Object service);
    void removeService(String name);
}
