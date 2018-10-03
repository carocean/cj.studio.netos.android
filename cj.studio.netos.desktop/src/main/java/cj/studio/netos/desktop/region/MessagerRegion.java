package cj.studio.netos.desktop.region;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cj.studio.netos.desktop.R;
import cj.studio.netos.framework.IReciever;
import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.annotation.Reciever;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.ViewportRegion;
import cj.studio.netos.framework.view.CJBottomNavigationView;

@ViewportRegion(name = "messager")
public class MessagerRegion extends Fragment {
    @ServiceSite
    IServiceProvider site;
    @Reciever
    IReciever reciever;
    CollapsingToolbarLayout toolbarLayout;
    CJBottomNavigationView bottomNavigationView;

    private List initData() {
        List mDatas = new ArrayList<String>(Arrays.asList("网域桌面模拟AppleStore首页", "所有信息均由应用发布", "为不当中国人 台留学生向蔡英文筹钱告挪威", "网域桌面模拟AppleStore首页", "所有信息均由应用发布", "为不当中国人 台留学生向蔡英文筹钱告挪威", "网域桌面模拟AppleStore首页", "所有信息均由应用发布", "为不当中国人 台留学生向蔡英文筹钱告挪威", "网域桌面模拟AppleStore首页", "所有信息均由应用发布", "为不当中国人 台留学生向蔡英文筹钱告挪威"));
        return mDatas;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toolbarLayout= getActivity().findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("消息");
        bottomNavigationView=getActivity().findViewById(R.id.module_navigation);

        //如果不指定attachToRoot=false或inflater.inflate(R.layout.region_message,null)，则报此子fragment在父view中已存在的错误，该错误产生时其parent
        //ViewGroup parent = (ViewGroup) view.getParent();parent显示为不为null,必须parent显示为不为null为null时才不报错
        //以上错误的真实原因是：attachToRoot默认为true，会自动将当前fragment挂载到root上，所以再inflate相应的view上时会报其父视图中已存在此fragment的错误，因此attachToRoot必须设为false
        View view = inflater.inflate(R.layout.region_message, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler);
        mRecyclerView.setAdapter(new MessagerRecyclerAdapter(site, this.getContext(), initData()));
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this.getContext(), 55));
        return view;
    }


}
