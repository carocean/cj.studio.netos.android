package cj.studio.netos.desktop;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

@Viewport(name = "/")
public class DesktopViewport extends AppCompatActivity {
    @Reciever
    IReciever reciever;

    @ServiceSite
    IServiceProvider site;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_desktop);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IWorkbench workbench = site.getService("$.workbench");
        workbench.renderViewport(this);

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
}
