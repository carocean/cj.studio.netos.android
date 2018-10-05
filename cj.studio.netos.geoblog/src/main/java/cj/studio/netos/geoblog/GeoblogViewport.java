package cj.studio.netos.geoblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cj.studio.netos.framework.IServiceSite;
import cj.studio.netos.framework.IViewportRegionManager;
import cj.studio.netos.framework.IWorkbench;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.Viewport;

@Viewport(name = "/geoblog")
public class GeoblogViewport extends AppCompatActivity {
    @ServiceSite
    IServiceSite site;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_geoblog);

        toolbar = findViewById(R.id.module_toolbar);

        initToolbar();

        IWorkbench workbench = site.getService("$.workbench");

        IViewportRegionManager viewportRegionManager = workbench.createRegionManager(this);
        viewportRegionManager.addRegion(new GeoblogRegion());
        viewportRegionManager.display("geoblog", R.id.module_display);

    }

    private void initToolbar() {
        toolbar.setTitle(R.string.module_geoblog);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //菜单点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
