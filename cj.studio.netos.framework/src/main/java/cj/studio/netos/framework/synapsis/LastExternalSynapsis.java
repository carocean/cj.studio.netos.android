package cj.studio.netos.framework.synapsis;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IAxon;
import cj.studio.netos.framework.ICell;
import cj.studio.netos.framework.IDendrite;
import cj.studio.netos.framework.INeuron;
import cj.studio.netos.framework.IReciever;
import cj.studio.netos.framework.ISynapsis;
import cj.studio.netos.framework.annotation.Viewport;

//调度请求到相应的viewport上
public class LastExternalSynapsis implements ISynapsis{
    Application application;
    public LastExternalSynapsis(Application application,ActivityInfo[] activities) {
        this.application=application;

        ICell cell=((INeuron)application).cell();
        for(ActivityInfo activityInfo:activities){
            String className=activityInfo.name;
            try {
                Class<?> clazz= Class.forName(className);
                if(!Activity.class.isAssignableFrom(clazz)){
                    throw new RuntimeException(String.format("类：%s 必须是Activity的派生类",className));
                }
                Viewport viewport=clazz.getAnnotation(Viewport.class);
                if(viewport==null){
                    throw new RuntimeException(String.format("类：%s 缺少注解@Viewport",className));
                }
                String name=viewport.name();
                if(!name.startsWith("/")){
                    throw new RuntimeException(String.format("类：%s 必须以/号开头",className));
                }
                if(!"/".equals(name)&&name.endsWith("/")){
                    name=name.substring(0,name.length()-1);
                }
                IDendrite dendrite=new Dendrite(clazz);
                cell.addDendrite(name,dendrite);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void flow(Frame frame, IAxon axon) {
        ICell cell=((INeuron)application).cell();
        String path=frame.withoutSharpSymbolsPath();
        IDendrite dendrite=cell.dendrite(path);
        if(dendrite==null){
            throw new RuntimeException(String.format("请求的视口未发现:%s",frame.path()));
        }
        dendrite.flow(frame);
    }

    @Override
    public void dispose() {
        application=null;
    }

    private class Dendrite implements IDendrite {
        Class<?> viewportClazz;
        IReciever reciever;
        public Dendrite(Class<?> clazz) {
            viewportClazz=clazz;
        }

        public void setReciever(IReciever reciever) {
            this.reciever = reciever;
        }

        @Override
        public void flow(Frame frame) {
            if("navigate".equals(frame.command())||"open".equals(frame.command())) {
                doNavigate(frame);
                return;
            }
            if(reciever==null){
                return;
            }
            reciever.fire(frame);
        }

        private void doNavigate(Frame frame) {
            Intent intent = new Intent();
            Uri uri = Uri.parse(frame.toString());
            intent.setData(uri);
            String[] heads = frame.enumHeadName();
            for (String key : heads) {
                intent.putExtra(String.format("$.head.%s", key), frame.head(key));
            }
            String[] params = frame.enumParameterName();
            for (String key : params) {
                intent.putExtra(String.format("$.parameter.%s", key), frame.parameter(key));
            }
            if(frame.content().size()>0){
                intent.putExtra("$.content",frame.content().toByteArray());
            }
            intent.setClass(application, viewportClazz);
            application.startActivity(intent);
        }
    }
}
