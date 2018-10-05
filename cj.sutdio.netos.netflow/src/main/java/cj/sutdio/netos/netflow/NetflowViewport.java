package cj.sutdio.netos.netflow;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cj.studio.netos.framework.IServiceProvider;
import cj.studio.netos.framework.IWorkbench;
import cj.studio.netos.framework.annotation.ServiceSite;
import cj.studio.netos.framework.annotation.Viewport;
import cj.studio.netos.framework.view.CJBottomNavigationView;

@Viewport(name = "/netflow")
public class NetflowViewport extends AppCompatActivity {
    @ServiceSite
    IServiceProvider site;
    Toolbar toolbar;
    CJBottomNavigationView bottomNavigationView;

    private List initData() {
        List mDatas = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,1,2,3,4,5,6));
        return mDatas;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_netflow);

        toolbar = findViewById(R.id.module_toolbar);
        bottomNavigationView=findViewById(R.id.module_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle();
        installBottomMenu();

        RecyclerView mRecyclerView = findViewById(R.id.channels_recycler);
        mRecyclerView.setAdapter(new MyRecyclerAdapter(this, initData()));
    }

    private void installBottomMenu() {
        bottomNavigationView.inflateMenu(R.menu.module_netflow);
    }

    private void setTitle() {
        toolbar.setTitle("网流");
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

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {

        private Context mContext;
        private List<Integer> mDatas;

        public MyRecyclerAdapter(Context context, List<Integer> datas) {
            super();
            this.mContext = context;
            this.mDatas = datas;
        }

        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        // 填充onCreateViewHolder方法返回的holder中的控件
        public void onBindViewHolder(MyHolder holder, int position) {
            // TODO Auto-generated method stub
            holder.textView.setText(mDatas.get(position)+"--");
        }

        @Override
        // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
        public MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            // 填充布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_netflow, null);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        // 定义内部类继承ViewHolder
        class MyHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public MyHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.test_textView);
            }

        }


    }
}
