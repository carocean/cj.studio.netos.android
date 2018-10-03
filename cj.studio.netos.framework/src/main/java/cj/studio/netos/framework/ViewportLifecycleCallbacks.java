package cj.studio.netos.framework;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Map;

import cj.studio.netos.framework.annotation.Reciever;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.Viewport;

class ViewportLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        Map<String, IDendrite> dendriteMap;
        IServiceSite site;
        public ViewportLifecycleCallbacks(Map<String, IDendrite> dendriteMap,IServiceSite site) {
            this.dendriteMap = dendriteMap;
            this.site=site;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Class<?> clazz = activity.getClass();
            Viewport viewport = clazz.getAnnotation(Viewport.class);
            if (viewport == null) {
                return;
            }
            injectFields(activity, clazz,viewport);
        }



    private void injectFields(Activity activity, Class<?> clazz, Viewport viewport) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ServiceSite serviceSite = field.getAnnotation(ServiceSite.class);
                if (serviceSite != null) {
                    field.setAccessible(true);
                    try {
                        field.set(activity, site);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Reciever reciever=field.getAnnotation(Reciever.class);
                if (reciever != null) {
                    field.setAccessible(true);
                    try {
                        IReciever recobj=new InternalReciever();
                        field.set(activity, recobj);
                        IDendrite dendrite=dendriteMap.get(viewport.name());
                        if(dendrite!=null) {
                            dendrite.setReciever(recobj);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    continue;
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i("onActivityStarted", activity.toString());
            ISelection selection=site.getService("$.workbench.selection");
            selection.selectViewport(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Class<?> clazz = activity.getClass();
            Viewport viewport = clazz.getAnnotation(Viewport.class);
            if (viewport == null) {
                return;
            }
            uninjectFields(activity,clazz);

            ISelection selection=site.getService("$.workbench.selection");
            if(activity.equals(selection.selectedViewport())) {
                selection.selectViewport(null);
            }
        }

        private void uninjectFields(Activity activity, Class<?> clazz) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ServiceSite serviceSite = field.getAnnotation(ServiceSite.class);
                if (serviceSite != null) {
                    field.setAccessible(true);
                    try {
                        field.set(activity, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Reciever reciever=field.getAnnotation(Reciever.class);
                if (reciever != null) {
                    field.setAccessible(true);
                    try {
                        field.set(activity, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }
        }
    }