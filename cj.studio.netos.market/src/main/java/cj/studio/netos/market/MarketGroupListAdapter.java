package cj.studio.netos.market;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MarketGroupListAdapter extends RecyclerView.Adapter {


    @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market,null);
            RecyclerView.ViewHolder viewHolder=new MarketGroupListHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RecyclerView recyclerView=((MarketGroupListHolder)holder).recyclerView;

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        private class MarketGroupListHolder extends RecyclerView.ViewHolder {
            RecyclerView recyclerView;
            public MarketGroupListHolder(View view) {
                super(view);
                recyclerView=view.findViewById(R.id.item_market_recycler);
                RecyclerView.Adapter child=new MarketGroupItemListAdapter();
                recyclerView.setAdapter(child);

                recyclerView.addItemDecoration(new MyDividerItemDecoration(view.getContext(),60));
            }

        }

    }