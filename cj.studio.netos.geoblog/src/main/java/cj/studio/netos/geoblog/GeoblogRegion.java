package cj.studio.netos.geoblog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;

import java.util.ArrayList;

import cj.studio.netos.framework.annotation.ViewportRegion;
import cj.studio.netos.framework.poi.PoiLayout;
import cj.studio.netos.framework.poi.PoiListView;
import cj.studio.netos.framework.poi.PoiTextView;

@ViewportRegion(name = "geoblog")
public class GeoblogRegion extends Fragment implements PoiLayout.OnChangeListener {
    TextureMapView mMapView = null;
    AMap aMap;

    private PoiLayout poiLayout;
    private PoiListView list;
    private PoiTextView tvBottom;
    private PoiMapAdapter adapter;
    private CommenLoader<PoiModel> commenLoader;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.region_geoblog, container, false);

        //获取地图控件引用
        mMapView = (TextureMapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        handler = new Handler();
        initView(view);
        initList();
        initPoiLayout();
        getData(commenLoader.page);

        return view;
    }

    private void initView(View view) {
        poiLayout = (PoiLayout) view.findViewById(R.id.poi_layout);
        list = (PoiListView) view.findViewById(R.id.poi_list);
        tvBottom = (PoiTextView) view.findViewById(R.id.tv_bottom);
    }

    private void initList() {
        adapter = new PoiMapAdapter(getContext(), new ArrayList<PoiModel>(), R.layout.adapter_poi);
        list.setCanRefresh(false);
        list.setCanLoadMore(true);
        list.showAsList();
        list.setAdapter(adapter);
        commenLoader = new CommenLoader<>(list, adapter);
        commenLoader.setPageCount(10);
        commenLoader.setOnLoaderListener(new CommenLoader.OnLoaderListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                getData(commenLoader.page);
            }

            @Override
            public void loadSuccess() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void noContent() {

            }

            @Override
            public void loadError(boolean isEmpty) {

            }
        });
    }

    private void initPoiLayout() {
        poiLayout.setOnChangeListener(this);
        tvBottom.setOnTikListener(new PoiTextView.OnTikListener() {
            @Override
            public void onTik(View v) {
                poiLayout.toggle(PoiLayout.STATUS_EXTEND);
            }
        });
    }

    /**
     * 模拟数据获取
     */
    private void getData(final int page) {
        long delayMillis = page == 1 ? 0 : 2000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (isFinishing()) {
//                    return;
//                }
                ArrayList<PoiModel> datas = new ArrayList<>();
                int count = page < 10 ? 10 : 6;
                for (int i = 0; i < count; i++) {
                    int index = 10 * (page - 1) + i;
                    datas.add(new PoiModel("标题:" + index, "xxxxxxxxxx" + index));
                }
                setData(datas);
            }
        }, delayMillis);
    }

    /**
     * 数据设置
     */
    private void setData(final ArrayList<PoiModel> data) {
        commenLoader.setData(data);
        if (commenLoader.page == 1) {
            if (data.size() > 0) {
                poiLayout.setVisibility(View.VISIBLE);
                poiLayout.toggle(PoiLayout.STATUS_DEFAULT);
            }
            list.scrollToPosition(0);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }

    @Override
    public void onChange(int status) {
        tvBottom.setVisibility(status == PoiLayout.STATUS_CLOSE ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onScroll(float offset) {
        tvBottom.setVisibility(offset == 1 ? View.VISIBLE : View.GONE);
    }
}
