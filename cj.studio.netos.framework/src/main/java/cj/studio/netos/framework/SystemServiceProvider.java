package cj.studio.netos.framework;

import android.app.Application;

class SystemServiceProvider implements IServiceProvider {
    String label;
    Application application;

    public SystemServiceProvider(Application application) {
        label = "$.android.";
        this.application = application;
    }

    @Override
    public Object getService(String name) {
        int pos = name.indexOf(label);
        if (pos == 0) {//访问系统服务
            String sname = name.substring(label.length(), name.length());
            return application.getSystemService(sname);
        }
        return null;
    }

    @Override
    public <T> T getService(Class<T> clazz) {

        return null;
    }
}