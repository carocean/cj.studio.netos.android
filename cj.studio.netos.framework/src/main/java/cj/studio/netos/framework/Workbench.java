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
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏

        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
//        StatusBarUtil.setRootViewFitsSystemWindows(activity,true);
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(activity);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(activity, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(activity,R.color.blue_status_bar);
//        }

    }

    @Override
    public IViewportRegionManager createRegionManager(FragmentActivity viewport) {
        return new ViewportRegionManager(viewport,runtimeServiceSite);
    }
}
