package cj.studio.netos.market;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cj.studio.netos.framework.annotation.Viewport;
import cj.studio.netos.framework.view.RecycleViewDivider;

@Viewport(name = "/market")
public class MarketViewport extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_market);

        toolbar=findViewById(R.id.module_toolbar);

        initToolbar();

        RecyclerView recyclerView=findViewById(R.id.market_recycler);
        RecyclerView.Adapter adapter=new MarketGroupListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecycleViewDivider(this, DividerItemDecoration.HORIZONTAL,30,getResources().getColor(R.color.gray_ececec)));

    }

    private void initToolbar() {
        toolbar.setTitle(R.string.module_market);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //菜单点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);

    }
}
