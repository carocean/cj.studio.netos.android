package cj.studio.netos.desktop.region;


import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cj.studio.netos.desktop.R;
import cj.studio.netos.framework.annotation.ViewportRegion;
import cj.studio.netos.framework.view.CJBottomNavigationView;

@ViewportRegion(name = "news")
public class NewsRegion extends Fragment {
    CollapsingToolbarLayout toolbarLayout;
    CJBottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toolbarLayout= getActivity().findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("新闻");

        bottomNavigationView=getActivity().findViewById(R.id.module_navigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);

        View view=inflater.inflate(R.layout.region_news, container
                , false);

        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden)return;
        toolbarLayout.setTitle("新闻");
        super.onHiddenChanged(hidden);
    }
}
