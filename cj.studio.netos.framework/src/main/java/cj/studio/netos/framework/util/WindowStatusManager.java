package cj.studio.netos.framework.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

public class WindowStatusManager  {
    public static void seat(Activity host) {
        //设置 paddingTop
//        int height=0;
        int height=getStatusBarHeight(host);
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
        int height=0;
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

}
