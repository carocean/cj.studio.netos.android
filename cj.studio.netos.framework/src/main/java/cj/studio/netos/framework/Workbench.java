package cj.studio.netos.framework;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cj.studio.netos.framework.util.StatusBarUtil;
import cj.studio.netos.framework.util.WindowStatusManager;


class Workbench implements ICell,IWorkbench {
    IServiceProvider parent;
    Map<String, IDendrite> dendriteMap;
    ISelection selection;
    boolean isExit;
    Handler mHandler;
    IServiceSite runtimeServiceSite;
    public Workbench(IServiceProvider parent) {
        this.parent = parent;
    }

    @Override
    public void refresh() {
        dendriteMap = new HashMap<>();
        selection = new Selection();
        runtimeServiceSite=new RuntimeServiceSite(this);

        ViewportLifecycleCallbacks viewportLifecycleCallbacks = new ViewportLifecycleCallbacks(dendriteMap, runtimeServiceSite);
        Application application = parent.getService(Application.class);
        application.registerActivityLifecycleCallbacks(viewportLifecycleCallbacks);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
    }

    @Override
    public void addDendrite(String name, IDendrite dendrite) {
        dendriteMap.put(name, dendrite);
    }

    @Override
    public IDendrite dendrite(String path) {
        return dendriteMap.get(path);
    }

    @Override
    public void removeDendrite(String name) {
        dendriteMap.remove(name);
    }

    @Override
    public IAxon outputAxon() {
        return parent.getService("$.axon.output");
    }

    @Override
    public void close() {

    }

    @Override
    public <T> T getService(String name) {
        if ("$.workbench.selection".equals(name)) {
            return (T) selection;
        }
        return parent.getService(name);
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        return parent.getService(clazz);
    }


    /**
     * 点击两次退出程序
     */
    private void exit(Activity activity) {
        if (!isExit) {
            isExit = true;
            Toast.makeText(activity.getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            activity.finish();
            //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
            System.exit(0);
        }
    }

    @Override
    public WebView createWebView(Activity parent) {
        return null;
    }


    @Override
    public void monitorExit(Activity activity) {
        exit(activity);

    }

    @Override
    public void renderViewport(Activity activity) {
        WindowStatusManager.seat(activity);
        WindowStatusManager.adjustNavigationBarHeight(activity,true);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏

    }
    @Override
    public void renderViewportFullWindow(Activity activity) {
        WindowStatusManager.seatToTop(activity);
        WindowStatusManager.adjustNavigationBarHeight(activity,false);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏


        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(activity);


    }
    @Override
    public IViewportRegionManager createRegionManager(FragmentActivity viewport) {
        return new ViewportRegionManager(viewport,runtimeServiceSite);
    }
}
