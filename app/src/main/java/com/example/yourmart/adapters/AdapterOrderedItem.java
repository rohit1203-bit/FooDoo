package com.example.yourmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourmart.R;
import com.example.yourmart.models.ModelCartItem;
import com.example.yourmart.models.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{
    //view holder class
    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemArrayList) {
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterOrderedItem.HolderOrderedItem holder, int position) {
        ModelOrderedItem modelOrderedItem= orderedItemArrayList.get(position);

        String getId=modelOrderedItem.getpId();
        String name=modelOrderedItem.getName();
        String cost=modelOrderedItem.getCost();
        String price=modelOrderedItem.getPrice();
        String quantity=modelOrderedItem.getQuantity();

        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("₹"+price);
        holder.itemTitleTv.setText("₹"+cost);
        holder.itemTitleTv.setText("[" + quantity + "]");




    }

    @Override
    public int getItemCount() {
        return orderedItemArrayList.size();  //return list size
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //view of row_orderedItem.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;

        public HolderOrderedItem(@NonNull  View itemView) {
            super(itemView);

            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);

        }
    }

}
