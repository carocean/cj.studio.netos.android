package cj.studio.netos.market;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MarketGroupItemListAdapter extends RecyclerView.Adapter {


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_recycler_item, null);
        RecyclerView.ViewHolder viewHolder = new MarketGroupItemListAdapter.MarketGroupItemListHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    private class MarketGroupItemListHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MarketGroupItemListHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_title);
        }
    }
}