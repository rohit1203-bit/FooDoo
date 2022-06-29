package com.example.yourmart;

import android.widget.Filter;

import com.example.yourmart.adapters.AdapterProductSeller;
import com.example.yourmart.adapters.AdapterProductUser;
import com.example.yourmart.models.ModelProduct;

import java.util.ArrayList;

public class FilterProductUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList){
        this.adapter =adapter;
        this.filterList= filterList;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //validate data for search Query
        if (constraint!=null && constraint.length()>0){
            //search Filed not Empty , Searching something Perform search


            //change to upper case ,to make case in sensitive
             constraint = constraint.toString().toUpperCase();
            //store our filterd list
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                      filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            //search Filed  Empty , not searching , return original /all/complrte list

            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList=(ArrayList<ModelProduct> )results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();

    }
}
