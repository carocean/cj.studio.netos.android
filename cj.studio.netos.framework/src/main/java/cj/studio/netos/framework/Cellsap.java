package cj.studio.netos.framework;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import cj.studio.netos.framework.util.StringUtil;

/**
 * Created by caroceanjofers on 2018/1/26.
 */

public class Cellsap implements ICellsap {
    IServiceProvider parent;
    public Cellsap(IServiceProvider parent){
        this.parent=parent;
    }
    protected SharedPreferences getSharedPreferences(){
        Application app=parent.getService(Application.class);
        SharedPreferences sp =app.getSharedPreferences("evidences", Context.MODE_PRIVATE);

        return sp;
    }
    @Override
    public String[] remoteAddressList() {
        SharedPreferences sp=getSharedPreferences();
        Set<String> set=sp.getStringSet("remoteAddressList",new HashSet<String>());
        return set.toArray(new String[0]);
    }

    @Override
    public String principal() {
        SharedPreferences sp=getSharedPreferences();
        return sp.getString("principal","");
    }

    @Override
    public String token() {
        SharedPreferences sp=getSharedPreferences();
        return sp.getString("token","");
    }
    @Override
    public void token(String token) {
        SharedPreferences sp=getSharedPreferences();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("token",token);
        editor.apply();
    }
    @Override
    public void principal(String token) {
        SharedPreferences sp=getSharedPreferences();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("principal",token);
        editor.apply();
    }
    @Override
    public void remoteAddressList(String[] remoteAddressList) {
        SharedPreferences sp=getSharedPreferences();
        SharedPreferences.Editor editor=sp.edit();
        Set<String> set=new HashSet<>();
        for(String add:remoteAddressList){
            set.add(add);
        }
        editor.putStringSet("remoteAddressList",set);
        editor.apply();
    }
    @Override
    public void flush(){
        SharedPreferences sp=getSharedPreferences();
        SharedPreferences.Editor editor=sp.edit();
        editor.commit();
    }
    @Override
    public void empty(){
        SharedPreferences sp=getSharedPreferences();
        SharedPreferences.Editor editor=sp.edit();

        editor.apply();
        editor.clear();
        editor.commit();
    }
    //检查本地身份，如果合法则不过期则返回true，如果返回false则需要调用者跳转到登录窗口
     @Override
    public boolean checkIdentityIsEmpty(){
        SharedPreferences sp=getSharedPreferences();
        String token=sp.getString("token","");
        String principal=sp.getString("principal","");
        return (StringUtil.isEmpty(principal)||StringUtil.isEmpty(token))?true:false;
    }
}
