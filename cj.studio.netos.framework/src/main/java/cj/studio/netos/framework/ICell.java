package cj.studio.netos.framework;

import java.util.Set;

/**
 * Created by caroceanjofers on 2018/1/19.
 * 胞体，提供营养，它是模块和服务容器
 */

public interface ICell extends ICloseable,IServiceProvider {
    void refresh();
    IAxon outputAxon();

    void addDendrite(String name, IDendrite dendrite);

    IDendrite dendrite(String name);
    void removeDendrite(String name);
}
