package com.example.yourmart.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourmart.FilterProductUser;
import com.example.yourmart.R;
import com.example.yourmart.activities.EditProductActivity;
import com.example.yourmart.FilterProduct;
import com.example.yourmart.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList =productList;
    }

    @NonNull

    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterProductSeller.HolderProductSeller holder, int position) {
     //get data
     ModelProduct modelProduct = productList.get(position);
     String id = modelProduct.getProductId();
     String uid = modelProduct.getUid();
     String discountAvailable= modelProduct.getDiscountAvailable();
     String discountNote = modelProduct.getDiscountNote();
     String discountPrice = modelProduct.getDiscountPrice();
     String productCategory = modelProduct.getProductCategory();
     String productDescription = modelProduct.getProductDescription();
     String icon = modelProduct.getProductIcon();
     String quantity = modelProduct.getProductQuantity();
     String title = modelProduct.getProductTitle();
     String timestamp = modelProduct.getTimestamp();
     String originalPrice = modelProduct.getOriginalPrice();

     //set data
     holder.titleTv.setText(title);
     holder.quantityTv.setText(quantity);
     holder.discountedNoteTv.setText(discountNote);
     holder.discountedPriceTv.setText("₹"+discountPrice);
     holder.OriginalPriceTv.setText("₹"+ originalPrice);
     if(discountAvailable.equals("true")){
         //product is on discount
         holder.discountedPriceTv.setVisibility(View.VISIBLE);
         holder.discountedNoteTv.setVisibility(View.VISIBLE);
         holder.OriginalPriceTv.setPaintFlags(holder.OriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through original price
     }
     else {
         //product does not have discount
         holder.discountedPriceTv.setVisibility(View.GONE);
         holder.discountedNoteTv.setVisibility(View.GONE);
         holder.OriginalPriceTv.setPaintFlags(0);
     }
     try{
         Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);
     }catch (Exception e){
         holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
     }

     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             //handle item Clicks , show item details
             detailsBottomSheet(modelProduct);
         }
     });



    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
        //infilate vieew for bootom
        View view= LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller,null);
        //set view to bottom sheet
        bottomSheetDialog.setContentView(view);



        //init vieew of bottom sheet
        ImageButton backBtn=view.findViewById(R.id.backBtn);
        ImageButton deleteBtn=view.findViewById(R.id.deleteBtn);
        ImageButton editBtn=view.findViewById(R.id.editBtn);
        ImageView productIconIv=view.findViewById(R.id.productIconIv);
        TextView discountNoteTv=view.findViewById(R.id.discountNoteTv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView discountedPriceTv=view.findViewById(R.id.discountedPriceTv);
        TextView OriginalPriceTv=view.findViewById(R.id.OriginalPriceTv);


        //get data

        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable= modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountNoteTv.setText(discountNote);
        discountedPriceTv.setText("₹"+discountPrice);
        OriginalPriceTv.setText("₹"+originalPrice);
        productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);

        if(discountAvailable.equals("true")){
            //product is on discount
            discountedPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            OriginalPriceTv.setPaintFlags(OriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through original price
        }
        else {
            //product does not have discount
            discountedPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_primary).into(productIconIv);
        }
        catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        //show dialog
        bottomSheetDialog.show();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                //show delete confirm dialog
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are You sure You want To DELETE Product"+title+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete
                                deleteProduct(id);//id is product id
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                            }
                        }).show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });





    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Product deleted...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProduct(this,filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder {

        //holds views of recycler view

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,
                OriginalPriceTv;

        public HolderProductSeller(@NonNull  View itemView) {
            super(itemView);
            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            OriginalPriceTv=itemView.findViewById(R.id.OriginalPriceTv);


        }
    }
}
