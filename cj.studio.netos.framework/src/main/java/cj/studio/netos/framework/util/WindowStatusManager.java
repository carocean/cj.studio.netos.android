package cj.studio.netos.framework.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

public class WindowStatusManager {
    public static void seat(Activity host) {
        //设置 paddingTop
//        int height=0;
        int height = getStatusBarHeight(host);
        ViewGroup rootView = (ViewGroup) host.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setPadding(0, height, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            host.getWindow().setStatusBarColor(Color.parseColor("#3F51B5"));
        } else {
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) host.getWindow().getDecorView();
            View statusBarView = new View(host);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    height);
            statusBarView.setBackgroundColor(Color.parseColor("#3F51B5"));
            decorView.addView(statusBarView, lp);
        }
    }

    public static void seatToTop(Activity host) {
        //设置 paddingTop
        int height = 0;
        ViewGroup rootView = (ViewGroup) host.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setPadding(0, height, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            host.getWindow().setStatusBarColor(Color.parseColor("#3F51B5"));
        } else {
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) host.getWindow().getDecorView();
            View statusBarView = new View(host);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    height);
            statusBarView.setBackgroundColor(Color.parseColor("#3F51B5"));
            decorView.addView(statusBarView, lp);
        }
    }

    /**
     * 利用反射获取状态栏高度
     *
     * @return
     */
    private static int getStatusBarHeight(Activity host) {
        Resources resources = host.getResources();
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getNavigationBarHeight(Activity host) {


        Resources resources = host.getResources();

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

//获取NavigationBar的高度

        int height = resources.getDimensionPixelSize(resourceId);

        return height;


    }
    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @param window  当前窗口
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    public static boolean checkNavigationBarShow(@NonNull Context context, @NonNull Window window) {
        boolean show;
        Display display = window.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);

        View decorView = window.getDecorView();
        Configuration conf = context.getResources().getConfiguration();
        if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
            View contentView = decorView.findViewById(android.R.id.content);
            show = (point.x != contentView.getWidth());
        } else {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            show = (rect.bottom != point.y);
        }
        return show;
    }
    public static void adjustNavigationBarHeight(Activity host,boolean keepStatusTop) {
//        boolean hasMenuKey = ViewConfiguration.get(host).hasPermanentMenuKey();
//        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//        if (!hasMenuKey && !hasBackKey) {
//            host.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0,
//                    keepStatusTop?getStatusBarHeight(host):0, 0, getNavigationBarHeight(host));
//        }
        if(checkNavigationBarShow(host,host.getWindow())) {
            host.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0,
                    keepStatusTop?getStatusBarHeight(host):0, 0, getNavigationBarHeight(host));
        }
    }
}
