package cj.studio.netos.framework;

import android.support.v4.app.Fragment;

public interface IViewportRegionManager extends IRecieverCallback{
    void addRegion(Fragment fragment);

    void display(String name, int displayResid);
}
