package com.example.yourmart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourmart.adapters.AdapterOrderShop;
import com.example.yourmart.adapters.AdapterProductSeller;
import com.example.yourmart.Constants;
import com.example.yourmart.models.ModelOrderShop;
import com.example.yourmart.models.ModelOrderedItem;
import com.example.yourmart.models.ModelProduct;
import com.example.yourmart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
    private TextView nametv, shopnameTv03,emailTv03,
            filterProductTv,tabProductsTv03,tabOrdersTv03,filteredOrderTv;
    private EditText searchProductET;
    private ImageButton logoutseller,editprofilrbtn,addProductBtn,filterProductBtn,filterOrderBtn,reviewsBtn,settingsBtn;
    private ImageView ProfileIv03;
    private RecyclerView productsRv,ordersRv;
    private RelativeLayout productRl03,ordersRl03;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        nametv=findViewById(R.id.nameTv);
        shopnameTv03=findViewById(R.id.shopnameTv03);
        emailTv03=findViewById(R.id.emailTv03);
        tabProductsTv03=findViewById(R.id.tabProductsTv03);
        filterProductTv=findViewById(R.id.filterProductTv);
        searchProductET=findViewById(R.id.searchProductET);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        tabOrdersTv03=findViewById(R.id.tabOrdersTv03);
        logoutseller=findViewById(R.id.Logoutseller);
        editprofilrbtn=findViewById(R.id.EditprofileBtn);
        addProductBtn=findViewById(R.id.addProductBtn);
        ProfileIv03=findViewById(R.id.ProfileIv03);
        productRl03=findViewById(R.id.productRl03);
        ordersRl03=findViewById(R.id.ordersRl03);
        productsRv=findViewById(R.id.productsRv);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);
        filteredOrderTv =findViewById(R.id.filteredOrderTv);
        ordersRv=findViewById(R.id.ordersRv);
        reviewsBtn=findViewById(R.id.reviewsBtn);
        settingsBtn=findViewById(R.id.settingsBtn);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();

        loadAllProducts();
        loadAllOrders();

        showProductsUI();
        //search
        searchProductET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        logoutseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffLine();





            }
        });

        editprofilrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Product
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));


            }
        });
        tabProductsTv03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //load Products
                showProductsUI();
            }
        });

        tabOrdersTv03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load Order
                showOrdersUI();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            //    get selected item
                                String selected=Constants.productCategories1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadAllProducts();
                                }
                                else {
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                .show();
            }
        });

        filteredOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options= {"All","In Progress","Completed","Cancelled"};

                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    filteredOrderTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter("");
                                }
                                else {
                                    String optionClicked = options[which];
                                    filteredOrderTv.setText("Showing"+optionClicked+"Orders");
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }

                            }
                        });
            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //same activity as user
                Intent intent= new Intent(MainSellerActivity.this,ShopReviewsActivity.class);
                intent.putExtra("shopUid",""+firebaseAuth.getUid());
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this,SettingsActivity.class));
            }
        });




    }

    private void loadAllOrders() {

        orderShopArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ModelOrderShop modelOrderShop= ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop = new AdapterOrderShop(MainSellerActivity.this,orderShopArrayList);
                        ordersRv.setAdapter(adapterOrderShop);

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

        //get all Products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();
                            //if selected category matches product category then add in list
                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }


                        }
                        //setup adapter
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        //get all Products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }


    private void showProductsUI() {
        productRl03.setVisibility(View.VISIBLE);
        ordersRl03.setVisibility(View.GONE);

        tabProductsTv03.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv03.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv03.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv03.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show products Ui and hide orders ui
        productRl03.setVisibility(View.GONE);
        ordersRl03.setVisibility(View.VISIBLE);

        tabProductsTv03.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv03.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        tabOrdersTv03.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv03.setBackgroundResource(R.drawable.shape_rect04);

    }

    private void makeMeOffLine() {
        progressDialog.setMessage("Logging out...");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user== null){
            startActivity(new Intent(MainSellerActivity.this, MainActivity.class));
            finish();


        }
        else {
            loadMyInfo();
        }

    }




    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){

                            String name= ""+ds.child("name").getValue();
                            String accountType= ""+ds.child("accountType").getValue();
                            String email= ""+ds.child("email").getValue();
                            String profileimage= ""+ds.child("profileimage").getValue();
                            String shopName= ""+ds.child("shopName").getValue();




                            nametv.setText(name);
                            shopnameTv03.setText(shopName);
                            emailTv03.setText(email);
                            try {
                                Picasso.get().load(profileimage).placeholder(R.drawable.ic_store_grey).into(ProfileIv03);
                            }
                            catch (Exception e){
                                ProfileIv03.setImageResource(R.drawable.ic_store_grey);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




}