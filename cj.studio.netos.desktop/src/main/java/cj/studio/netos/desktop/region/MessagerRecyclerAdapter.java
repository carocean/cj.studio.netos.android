package cj.studio.netos.desktop.region;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cj.studio.netos.desktop.R;
import cj.studio.netos.framework.Frame;
import cj.studio.netos.framework.IRequester;
import cj.studio.netos.framework.IServiceProvider;

public class MessagerRecyclerAdapter extends RecyclerView.Adapter<MessagerRecyclerAdapter.MyHolder> {

    private Context mContext;
    private List<String> mDatas;
    IServiceProvider site;

    public MessagerRecyclerAdapter(IServiceProvider site, Context context, List<String> datas) {
        super();
        this.mContext = context;
        this.mDatas = datas;
        this.site = site;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    // 填充onCreateViewHolder方法返回的holder中的控件
    public void onBindViewHolder(MessagerRecyclerAdapter.MyHolder holder, int position) {
        // TODO Auto-generated method stub
        holder.session_summary.setText(mDatas.get(position) + "--");
    }

    @Override
    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    public MessagerRecyclerAdapter.MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
        MessagerRecyclerAdapter.MyHolder holder = new MessagerRecyclerAdapter.MyHolder(view);
        return holder;
    }

    // 定义内部类继承ViewHolder
    class MyHolder extends RecyclerView.ViewHolder {

        private TextView session_summary;

        public MyHolder(final View view) {
            super(view);
            session_summary = (TextView) view.findViewById(R.id.session_summary);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("item selected", session_summary.getText() + "");
                    IRequester requester = site.getService("$.requester");
                    Frame frame = new Frame("navigate /desktop/messager/viewer netos/1.0");
                    requester.request(frame);

                    Toast.makeText(view.getContext(), session_summary.getText() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}