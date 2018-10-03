package cj.studio.netos.desktop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pedaily.yc.ycdialoglib.bottomLayout.BottomDialogFragment;
import com.pedaily.yc.ycdialoglib.bottomMenu.CustomBottomDialog;
import com.pedaily.yc.ycdialoglib.bottomMenu.CustomItem;
import com.pedaily.yc.ycdialoglib.bottomMenu.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.ICellsap;
import cj.studio.netos.framework.IReciever;
import cj.studio.netos.framework.IRequester;
import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.util.WindowUtil;
import cj.studio.netos.framework.view.BadgeRadioButton;
import cj.studio.netos.framework.view.CJBottomNavigationView;

public class FloatingActionButtonOnclickListener implements View.OnClickListener {
    private IServiceProvider site;
    AppCompatActivity on;
    public FloatingActionButtonOnclickListener(Activity on, IServiceProvider site) {
        this.on = (AppCompatActivity) on;
        this.site = site;
    }



    @Override
    public void onClick(View v) {
        BottomDialogFragment dialog = BottomDialogFragment.create(on.getSupportFragmentManager());
        BottomDialogFragment.ViewListener viewListener = new MyViewListener(on, dialog, site);
        dialog.setViewListener(viewListener);
        dialog.setLayoutRes(R.layout.dialog_bottom)
                .setDimAmount(0.5f)
                .setTag("BottomDialog")
                .setCancelOutside(true)
                .setHeight(WindowUtil.getScreenHeight(on) / 2 - 180)
                .show();
    }


    class MyViewListener extends Dialog implements BottomDialogFragment.ViewListener {
        private final ICellsap cellsap;
        AppCompatActivity on;
        List<CustomItem> items;
        BottomDialogFragment dialog;

        public MyViewListener(AppCompatActivity on, BottomDialogFragment dialog, IServiceProvider site) {
            super(on.getApplication());
            this.on = on;
            items = new ArrayList<>();
            this.dialog = dialog;
            this.cellsap = site.getService("$.cellsap");
        }

        @SuppressLint("RestrictedApi")
        void inflateMenu(int menu) {
            MenuInflater menuInflater = new SupportMenuInflater(getContext());
            MenuBuilder menuBuilder = new MenuBuilder(getContext());
            menuInflater.inflate(menu, menuBuilder);

            for (int i = 0; i < menuBuilder.size(); i++) {
                MenuItem menuItem = menuBuilder.getItem(i);
                items.add(new CustomItem(menuItem.getItemId(), menuItem.getTitle().toString(), menuItem.getIcon()));
            }

        }

        @Override
        public void bindView(View v) {
            String principal = cellsap.principal();

            TextView textView = v.findViewById(R.id.tv_title);
            textView.setText(String.format("%s 的 netos", principal));

            final RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.popup_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(on));
            inflateMenu(R.menu.desktop_popup);

            DesktopPopupAdapter mAdapter = new DesktopPopupAdapter(on, items, CustomBottomDialog.VERTICAL);
            mAdapter.setItemClick(new OnItemClickListener() {
                @Override
                public void click(CustomItem item) {
                    Log.i("切换desktop region", item.getTitle());
                    String name = on.getResources().getResourceName(item.getId());
                    int spliter = name.indexOf("/");
                    if (spliter > -1) {
                        name = name.substring(spliter + 1, name.length());
                    }
                    //切换desktop region
//                    if(regionNav.navigate(name)){
//                        dialog.dismiss();
//                    }
                }
            });

            recyclerView.setAdapter(mAdapter);

            final CJBottomNavigationView bottomNavigationView = v.findViewById(R.id.popup_navigation);
            if (bottomNavigationView.getChildCount() > 0) {
                BadgeRadioButton badgeRadioButton=(BadgeRadioButton)bottomNavigationView.getChildAt(0);
                badgeRadioButton.setBadgeNumber(22);
            }
            bottomNavigationView.setOnCheckedChangeListener(new CJBottomNavigationView.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String name = on.getResources().getResourceName(checkedId);
                    int spliter = name.indexOf("/");
                    if (spliter > -1) {
                        name = name.substring(spliter + 1, name.length());
                    }
                    IRequester requester=site.getService("$.requester");
                    Frame frame=new Frame(String.format("navigate /%s netos/1.0",name));
                    requester.request(frame);
//                    if(navigation.navigate(name)) {
                        dialog.dismiss();
//                    }
                    return ;
                }


            });
        }
    }
}