package cj.studio.netos.framework;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

/**
 * Created by caroceanjofers on 2018/1/18.
 */

public interface IWorkbench extends ICell {
    WebView createWebView(Activity parent);


    void monitorExit(Activity viewport);

    void renderViewport(Activity viewport);
    void renderViewportFullWindow(Activity viewport);
    IViewportRegionManager createRegionManager(FragmentActivity viewport);
}
