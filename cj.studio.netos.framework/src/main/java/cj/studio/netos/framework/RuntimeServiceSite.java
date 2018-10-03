package cj.studio.netos.framework;

import java.util.HashMap;
import java.util.Map;

class RuntimeServiceSite implements IServiceSite{
        IServiceProvider parent;
        Map<String,Object> runtimeServices;
        public RuntimeServiceSite(IServiceProvider parent){
            this.parent=parent;
            runtimeServices=new HashMap<>();
        }
        @Override
        public void addService(String name, Object service) {
            if(runtimeServices.containsKey(name)){
                throw new RuntimeException("已存在服务："+name);
            }
            runtimeServices.put(name,service);
        }

        @Override
        public void removeService(String name) {
            runtimeServices.remove(name);
        }

        @Override
        public void dispose() {
            runtimeServices.clear();
        }

        @Override
        public <T> T getService(String name) {
            T obj=(T)runtimeServices.get(name);
            if(obj!=null){
                return obj;
            }
            return parent.getService(name);
        }

        @Override
        public <T> T getService(Class<T> clazz) {
            T obj=(T)runtimeServices.get(clazz.getName());
            if(obj!=null){
                return obj;
            }
            return parent.getService(clazz);
        }
    }