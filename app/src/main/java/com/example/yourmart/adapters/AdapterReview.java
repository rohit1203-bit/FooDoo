package com.example.yourmart.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourmart.R;
import com.example.yourmart.models.ModelReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview> {

    private Context context;
    private ArrayList<ModelReview> reviewArrayList;


    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }



    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolderReview holder, int position) {
        ModelReview modelReview =reviewArrayList.get(position);
        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String timeStamp = modelReview.getTimestamp();
        String review = modelReview.getReview();

        //we also need info (profile image, name) of user who wrote the review: we can do it using uid of user
        loadUserDetail(modelReview, holder);

        //convert timestamp to proper format dd/MM/yyyy

       // Calendar calendar= Calendar.getInstance();
      //  calendar.setTimeInMillis(Long.parseLong(timeStamp));
      //  String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        //set data

        holder.ratingBar.setRating (Float.parseFloat(ratings));
        holder.reviewTv.setText(review);
      //  holder.dateTv.setText(dateFormat);
    }

    private void loadUserDetail(ModelReview modelReview, HolderReview holder) {
        String uid = modelReview.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name= ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileimage").getValue();

                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                        }
                        catch (Exception e){
                        holder.profileIv.setImageResource(R.drawable.ic_store_grey);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }

                });

    }




    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{

        private ImageView profileIv;
        private TextView nameTv, dateTv, reviewTv;
        private RatingBar ratingBar;
        public HolderReview(@NonNull  View itemView) {
            super(itemView);


            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);

        }
    }

}
