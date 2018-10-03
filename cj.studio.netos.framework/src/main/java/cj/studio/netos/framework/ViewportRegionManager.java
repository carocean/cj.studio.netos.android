package cj.studio.netos.framework;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.studio.netos.framework.annotation.Reciever;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.ViewportRegion;
import cj.studio.netos.framework.util.StringUtil;

class ViewportRegionManager implements IViewportRegionManager, IRecieverCallback {
    FragmentActivity viewport;
    IServiceSite site;
    Map<String, Fragment> fragmentMap;

    public ViewportRegionManager(FragmentActivity viewport, IServiceSite runtimeServiceSite) {
        fragmentMap = new HashMap<>();
        this.viewport = viewport;
        this.site = runtimeServiceSite;
    }

    @Override
    public void addRegion(Fragment fragment) {
        Class<?> clazz = fragment.getClass();
        ViewportRegion viewportRegion = clazz.getAnnotation(ViewportRegion.class);
        if (viewportRegion == null) {
            Log.e("ViewportRegionManager", "该ViewportRegion未声明注解 @ViewportRegion:" + clazz);
            return;
        }
        String name = viewportRegion.name();
        if (name.indexOf("/") > -1) {
            Log.e("ViewportRegionManager", "格式错误，区域名不能是路径。注解 @ViewportRegion :" + clazz);
            return;
        }
        injectService(fragment, clazz);
        fragmentMap.put(name, fragment);
    }

    private void injectService(Fragment fragment, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ServiceSite serviceSite = field.getAnnotation(ServiceSite.class);
            if (serviceSite != null) {
                field.setAccessible(true);
                try {
                    field.set(fragment, site);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Reciever reciever = field.getAnnotation(Reciever.class);
            if (reciever != null) {
                field.setAccessible(true);
                try {
                    IReciever recobj = new InternalReciever();
                    field.set(fragment, recobj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                continue;
            }
        }
    }

    @Override
    public void display(String name, int displayResid) {
        Fragment fragment = fragmentMap.get(name);
        if (fragment == null) {
            Log.e("ViewportRegionManager", String.format("不存在名字为%s的fragment", name));
            return;
        }
        FragmentManager fragmentManager = viewport.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        try {
            List<Fragment> list = fragmentManager.getFragments();
            for (int i = 0; i < list.size(); i++) {
                Fragment old = list.get(i);
                View view = old.getView();
                if(displayResid==((View)view.getParent()).getId()) {
                    if (!old.isHidden()) {
                        transaction.hide(old);
                        transaction.addToBackStack(null);
                    }
                }
            }
            if (fragment.isAdded()) {
                if (fragment.isHidden()) {
                    transaction.show(fragment);
                    transaction.addToBackStack(null);
                }
            } else {
                transaction.add(displayResid, fragment).show(fragment);//addToBackStack不记录添加fragment过程
            }

            transaction.commit();
        } catch (Exception e) {
            Log.e("Host", e.getMessage());
        }
    }

    @Override
    public void onMessage(Frame frame) {
        //向各区域分发消息
        String regionName = frame.sharpSymbolsName();
        if (StringUtil.isEmpty(regionName)) {
            return;
        }
        Fragment fragment = this.fragmentMap.get(regionName);
        if (fragment == null) {
            Log.e("ViewportRegionManager", "请求的Region不存在：" + frame.url());
            return;
        }
        if ("navigate".equals(frame.command())) {
            String displayResId = frame.head("Display-ResourceId");
            if (StringUtil.isEmpty(displayResId)) {
                Log.e("ViewportRegionManager", "请求缺少Head：Display-ResourceId");
                return;
            }
            display(regionName, Integer.valueOf(displayResId));
            return;
        }
        Field[] fields = fragment.getClass().getDeclaredFields();
        for (Field field : fields) {
            Reciever reciever = field.getAnnotation(Reciever.class);
            if (reciever != null) {
                field.setAccessible(true);
                try {
                    IReciever recobj = (IReciever) field.get(fragment);
                    if (recobj != null) {
                        recobj.fire(frame);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
    }
}

