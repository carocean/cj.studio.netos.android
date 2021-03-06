package cj.studio.netos.desktop.region;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import cj.studio.netos.framework.view.BadgeRadioButton;
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
        bottomNavigationView=getActivity().findViewById(R.id.module_navigation);

        setTitle();
        setBottomVisibility(true);
        installBottomMenu();

        View view = inflater.inflate(R.layout.region_message, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler);
        mRecyclerView.setAdapter(new MessagerRecyclerAdapter(site, this.getContext(), initData()));
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this.getContext(), 55));
        return view;
    }
    private  void installBottomMenu(){
        bottomNavigationView.inflateMenu(R.menu.navigation_message);
        if(bottomNavigationView.getChildCount()>0) {
            BadgeRadioButton badgeRadioButton = (BadgeRadioButton) bottomNavigationView.getChildAt(0);
            badgeRadioButton.setBadgeNumber(332);
        }
    }
    private void setBottomVisibility(boolean b) {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void setTitle() {
        toolbarLayout.setTitle("消息");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden)return;
        setTitle();
        setBottomVisibility(true);
        super.onHiddenChanged(hidden);
    }

}
