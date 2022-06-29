package com.example.yourmart.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yourmart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {
    ImageButton gps02Btn;
    ImageView Profile02Iv;
    EditText Reg02name, Reg02phone, Regcountry, Regstate,
            Regcity, Regfulladdress, Reg02Email, Reg02pass, Reg02confirmpass, Reg02shopname, Reg02Deliveryfee;
    Button Reg02Btn;

    //permission constant
    private static final int LOCATION_REQUEST_CODE = 100;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    //Permission arrays
    private String[] locationPermission;
    private String[] cameraPermission;
    private String[] storagePermission;

    //image picked uri
    private Uri image_uri;


    private double latitude=0.0, longitude=0.0;

    private LocationManager locationmanager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        gps02Btn = findViewById(R.id.gps02Btn);
        Profile02Iv = findViewById(R.id.Profile02Iv);
        Reg02name = findViewById(R.id.Reg02name);
        Reg02shopname = findViewById(R.id.Reg02shopname);
        Reg02phone = findViewById(R.id.Reg02phone);
        Reg02Deliveryfee = findViewById(R.id.Reg02Deliveryfee);
        Regcountry = findViewById(R.id.Reg02Country);
        Regstate = findViewById(R.id.Reg02State);
        Regcity = findViewById(R.id.Reg02City);
        Regfulladdress = findViewById(R.id.Reg02Fulladdress);
        Reg02Email = findViewById(R.id.Reg02Email);
        Reg02pass = findViewById(R.id.Reg02pass);
        Reg02confirmpass = findViewById(R.id.Reg02confirmpass);
        Reg02Btn = findViewById(R.id.Reg01Btn);



        //initi permission array
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);


        gps02Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }
            }
        });
        Profile02Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                showImagePickDillog();
            }
        });

        Reg02Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Register seller
                inputData();
            }
        });
    }
    private String fullname, shopname ,phonenumber, deliveryfee, country, state , city, address,email,password,confirmpassword;
    private void inputData() {
        fullname = Reg02name.getText().toString().trim();
        shopname = Reg02shopname.getText().toString().trim();
        phonenumber = Reg02phone.getText().toString().trim();
        deliveryfee = Reg02Deliveryfee.getText().toString().trim();
        country = Regcountry.getText().toString().trim();
        state = Regstate.getText().toString().trim();
        city = Regcity.getText().toString().trim();
        address = Regfulladdress.getText().toString().trim();
        email = Reg02Email.getText().toString().trim();
        password = Reg02pass.getText().toString().trim();
        confirmpassword = Reg02confirmpass.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"Enter name..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(shopname)){
            Toast.makeText(this,"Enter shop Name..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this,"Enter Phone number..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deliveryfee)){
            Toast.makeText(this,"Enter Delivery fee..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(city  )){
            Toast.makeText(this,"Please click gps button to locate th location..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(state  )){
            Toast.makeText(this,"Please click gps button to locate th location..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(country  )){
            Toast.makeText(this,"Please click gps button to locate th location..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address  )){
            Toast.makeText(this,"Please click gps button to locate th location..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Pattern..",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            Toast.makeText(this,"Password must be At least 6 letters long...",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmpassword)){
            Toast.makeText(this,"Password Doesn't Match!!..",Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();

    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account..");
        progressDialog.show();

        //create Account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saverfirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverfirebaseData() {
        progressDialog.setMessage("Saving Account info...");

        String timestamp=""+System.currentTimeMillis();

        if (image_uri==null){
        //save data without image
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullname);
            hashMap.put("shopName",""+shopname);
            hashMap.put("Phone ",""+phonenumber);
            hashMap.put("Deliveryfee",""+deliveryfee);
            hashMap.put("country",""+country);
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("accountType","Seller");
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileimage","");

            //save to db
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //update
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, OtpGeneratorSeller.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this,OtpGeneratorSeller.class));
                            finish();

                        }
                    });


        }
        else {
            //save info with image
            //name and paths of image
            String filePathAndName="profile_images/"+""+firebaseAuth.getUid();
            //upload
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          //get uri of uploaded image
                            Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                //save data without image
                                HashMap<String ,Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("name",""+fullname);
                                hashMap.put("shopName",""+shopname);
                                hashMap.put("Phone ",""+phonenumber);
                                hashMap.put("Deliveryfee",""+deliveryfee);
                                hashMap.put("country",""+country);
                                hashMap.put("state",""+state);
                                hashMap.put("city",""+city);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("accountType","Seller");
                                hashMap.put("online","true");
                                hashMap.put("shopOpen","true");
                                hashMap.put("profileimage",""+downloadImageUri);//uri of uploaded image

                                //save to db
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //update
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this,OtpGeneratorSeller.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this,OtpGeneratorSeller.class));
                                                finish();

                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImagePickDillog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //camera clicked
                            if (checkCameraPermission()) {
                                pickFromCamera();
                            } else {
                                requestCameraPermission();

                            }
                        } else {
                            //gallary clicked
                            if (checkStoragePermission()) {
                                pickFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);


    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "Please Wait...", Toast.LENGTH_LONG).show();

        locationmanager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private void findAddress(){

        //find address,country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try{


          addresses= geocoder.getFromLocation(latitude,longitude, 1);


            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();
            String address= addresses.get(0).getAddressLine(0);//complete address

            //set address

            Regcountry.setText(country);
           Regcity.setText(city);
          Regstate.setText(state);
            Regfulladdress.setText(address);

        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }

    }


    private boolean checkLocationPermission(){
       boolean result=ContextCompat.checkSelfPermission(this,
               Manifest.permission.ACCESS_FINE_LOCATION) ==
               (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermission,LOCATION_REQUEST_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }




    @Override
    public void onLocationChanged(@NonNull Location location) {
           //location detected
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }










    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case LOCATION_REQUEST_CODE:{
               if (grantResults.length>0){
                   boolean locationAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;
                   if (locationAccepted){
                       //permission allowed
                       detectLocation();
                   }
                   else {
                       //permission denied
                       Toast.makeText(this,"Location Permission is necessary..",Toast.LENGTH_SHORT).show();
                   }
               }
           }
           break;

           case CAMERA_REQUEST_CODE:{
               if (grantResults.length>0){
                   boolean cameraAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;
                   boolean storageAccepted= grantResults[1]== PackageManager.PERMISSION_GRANTED;

                   if (cameraAccepted && storageAccepted ){
                       //permission allowed
                       pickFromCamera();
                   }
                   else {
                       //permission denied
                       Toast.makeText(this,"Camera Permission is necessary..",Toast.LENGTH_SHORT).show();
                   }
               }
           }
           break;
           case STORAGE_REQUEST_CODE :{
               if (grantResults.length>0){
                   boolean storageAccepted= grantResults[0]== PackageManager.PERMISSION_GRANTED;

                   if (storageAccepted){
                       //permission allowed
                       pickFromGallery();
                   }
                   else {
                       //permission denied
                       Toast.makeText(this,"Storage Permission is necessary..",Toast.LENGTH_SHORT).show();
                   }
               }
           }
           break;

       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          if(resultCode==RESULT_OK){
              if(requestCode==IMAGE_PICK_GALLERY_CODE){
                  image_uri=data.getData();
                  Profile02Iv.setImageURI(image_uri);

              } else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                  Profile02Iv.setImageURI(image_uri);
          }
          }

        super.onActivityResult(requestCode, resultCode, data);

    }

}