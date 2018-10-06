package cj.studio.netos.desktop;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cj.studio.netos.desktop.region.MessagerRegion;
import cj.studio.netos.desktop.region.NewsRegion;
import cj.studio.netos.framework.INeuron;
import cj.studio.netos.framework.IReciever;
import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.IViewportRegionManager;
import cj.studio.netos.framework.IWorkbench;
import cj.studio.netos.framework.annotation.Reciever;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.Viewport;
import cj.studio.netos.framework.util.OverflowToolbarMenu;
import cj.studio.netos.framework.util.StatusBarUtil;
import cj.studio.netos.framework.util.WindowStatusManager;

@Viewport(name = "/",isFullWindow = true)
public class DesktopViewport extends AppCompatActivity {
    @Reciever
    IReciever reciever;

    @ServiceSite
    IServiceProvider site;

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_desktop);

        appBarLayout=findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);

        initToolbarLayout();
        initToolbar();


        IWorkbench workbench = site.getService("$.workbench");

        IViewportRegionManager viewportRegionManager = workbench.createRegionManager(this);
        reciever.accept(viewportRegionManager);
        viewportRegionManager.addRegion(new MessagerRegion());
        viewportRegionManager.addRegion(new NewsRegion());

        viewportRegionManager.display("messager",R.id.desktop_display);
        //在本viewport上调自己的区域，会导致循环
//        IRequester requester=site.getService("$.requester");
//        Frame frame=new Frame("navigate /#messager netos/1.0");
//        requester.request(frame);

        FloatingActionButton fab = findViewById(R.id.desktop_fab);
//        fab.setBackgroundColor(0);
//        fab.setBackgroundResource(R.drawable.face);
        fab.setImageResource(R.mipmap.ic_face);
        FloatingActionButtonOnclickListener floatingActionButtonOnclickListener = new FloatingActionButtonOnclickListener(this, site,viewportRegionManager);
        fab.setOnClickListener(floatingActionButtonOnclickListener);

    }

    private void initToolbarLayout() {
        appBarLayout.addOnOffsetChangedListener(new MyAppBarLayoutOnOffsetChangedListener());
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.module_desktop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0&&getSupportFragmentManager().getBackStackEntryCount()<1) {
            INeuron neuron = ((INeuron) this.getApplication());
            IWorkbench workbench = neuron.cell().getService("$.workbench");
            workbench.monitorExit(this);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyAppBarLayoutOnOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener{
        boolean is_restore_status_bar;
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {//已经展开
//                    Log.i("....","已经展开");
                is_restore_status_bar=false;
                StatusBarUtil.setTranslucentStatus(DesktopViewport.this);
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                ////已经收缩完成
//                    Log.i("....","已经收缩完成");

                is_restore_status_bar=true;
            } else {
                //过程
//                    Log.i("....","过程");
                if(is_restore_status_bar){
                    return;
                }
                if(Math.abs(verticalOffset) <= appBarLayout.getTotalScrollRange()/2){
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                    Window window = getWindow();
                    View decorView = window.getDecorView();
                    //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                    decorView.setSystemUiVisibility(option);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.blue_status_bar));
                    //导航栏颜色也可以正常设置
                    //window.setNavigationBarColor(Color.TRANSPARENT);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Window window = getWindow();
                    WindowManager.LayoutParams attributes = window.getAttributes();
                    int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                    attributes.flags |= flagTranslucentStatus;
                    //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                    //attributes.flags |= flagTranslucentNavigation;
                    window.setAttributes(attributes);
                }
                is_restore_status_bar=true;
            }
        }
    }
}
