package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class customerVerifyAccount extends AppCompatActivity {

    LinearLayout lytUploadIC, lytUploadSelfie;
    ImageView iconUploadIC, iconUploadSelfie;
    MaterialIconView iconCheckIC, iconCheckSelfie, iconUncheckIC , iconUncheckSelfie ,mvCancel;
    Button btnSubmitVerification;
    private FirebaseFirestore db;
    int choose;
    Uri icUri, selfieUri;
    String icURL, selfieURL;


    SimpleDateFormat formatter;
    Date date;
    String todayDate, todayTime;

    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    StorageReference ICRef, selfieRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_verify_account);

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        lytUploadIC = findViewById(R.id.lytUploadIC);
        lytUploadSelfie = findViewById(R.id.lytUploadSelfie);

        iconCheckIC = findViewById(R.id.iconCheckIC);
        iconCheckSelfie = findViewById(R.id.iconCheckSelfie);
        iconUncheckIC = findViewById(R.id.iconUncheckIC);
        iconUncheckSelfie = findViewById(R.id.iconUncheckSelfie);

        iconUploadIC = findViewById(R.id.iconUploadIC);
        iconUploadSelfie = findViewById(R.id.iconUploadSelfie);

        btnSubmitVerification = findViewById(R.id.btnSubmitVerification);

        mvCancel = findViewById(R.id.mvCancel);

        //profilepicture select
        lytUploadIC.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                choose = 1;
                boolean pick = true;
                if (pick == true) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        PickImage();
                    }

                } else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        PickImage();
                    }
                }
            }
        });

        lytUploadSelfie.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                choose = 2;
                boolean pick = true;
                if (pick == true) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        PickImage();
                    }

                } else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        PickImage();
                    }
                }
            }
        });

        btnSubmitVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequestStatus();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), customerProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if(choose == 1){

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        ivIC.setImageBitmap(bitmap);
                        icUri = getImageUri(getApplicationContext(),bitmap);

                        iconCheckIC.setVisibility(View.VISIBLE);
                        iconUncheckSelfie.setVisibility(View.INVISIBLE);
                        iconUncheckIC.setVisibility(View.INVISIBLE);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

            if(choose == 2){

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        ivSelfie.setImageBitmap(bitmap);
                        selfieUri = getImageUri(getApplicationContext(),bitmap);

                       iconCheckSelfie.setVisibility(View.VISIBLE);
                        iconUncheckSelfie.setVisibility(View.INVISIBLE);
                        iconUncheckIC.setVisibility(View.INVISIBLE);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }


        }
    }

    private void uploadImage() {
        if(icUri == null && selfieUri == null)
        {
            Toast.makeText(getApplicationContext(),"Please upload both documents for verification your account.",Toast.LENGTH_LONG).show();

            iconUncheckIC.setVisibility(View.VISIBLE);
            iconUploadIC.setVisibility(View.INVISIBLE);
            iconCheckIC.setVisibility(View.INVISIBLE);

            iconUncheckSelfie.setVisibility(View.VISIBLE);
            iconUploadSelfie.setVisibility(View.INVISIBLE);
            iconCheckSelfie.setVisibility(View.INVISIBLE);

        }
        else if(icUri == null)
        {
            Toast.makeText(getApplicationContext(),"Please upload your IC images.",Toast.LENGTH_LONG).show();

            iconUncheckIC.setVisibility(View.VISIBLE);
            iconUploadIC.setVisibility(View.INVISIBLE);
            iconCheckIC.setVisibility(View.INVISIBLE);

        }
        else if(selfieUri == null)
        {
            Toast.makeText(getApplicationContext(),"Please upload a selfie with your IC.",Toast.LENGTH_LONG).show();

            iconUncheckSelfie.setVisibility(View.VISIBLE);
            iconUploadSelfie.setVisibility(View.INVISIBLE);
            iconCheckSelfie.setVisibility(View.INVISIBLE);
        }
        else{

            iconUncheckSelfie.setVisibility(View.INVISIBLE);
            iconUncheckIC.setVisibility(View.INVISIBLE);

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Preparing your request...");
            progressDialog.show();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            String fileName = formatter.format(now);

            ICRef = FirebaseStorage.getInstance().getReference("ic_images/"+ " identity_card " +fileName);
            selfieRef = FirebaseStorage.getInstance().getReference("selfie_images/"+ " selfie_with_ic " +fileName);

            ICRef.putFile(icUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ICRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    icURL = uri.toString();
                                    Log.e("URL ", "onSuccess: " + uri);

                                    selfieRef.putFile(selfieUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    selfieRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            selfieURL = uri.toString();
                                                            Log.e("URL ", "onSuccess: " + uri);

                                                            // send data to collection verification
                                                            verifyUser();

                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_SHORT).show();


                                        }
                                    });

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_SHORT).show();


                }
            });

        }
    }

    public void verifyUser(){

        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        date = new Date();
        todayDate = (formatter.format(date)).substring(0,10);
        todayTime = (formatter.format(date)).substring(11,16);

        DocumentReference nameRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameRef
                .update("userPictureIC", icURL, "userSelfieWithIC", selfieURL, "accountStatus", "pending", "RequestDate", todayDate, "RequestTime", todayTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditName", "DocumentSnapshot successfully updated!");

                        Intent intent = new Intent(getApplicationContext(),customerProfile.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Your account verification has been submitted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });
    }

    public void checkRequestStatus(){


        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e("ProfileActivity", "DocumentSnapshot data: " + document.getData());

                        String status = document.getData().get("accountStatus").toString();

                        if(status.equals("pending")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(customerVerifyAccount.this);

                            builder.setMessage("Please wait, your verification will be processed within 24 hours once all documents have been correctly submitted");
                            builder.setTitle("Your account verification are still pending.");
                            builder.setCancelable(false);

                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else if(status.equals("verified")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(customerVerifyAccount.this);

                            builder.setMessage("You are able to make any order");
                            builder.setTitle("Your account have been verified.");
                            builder.setCancelable(false);

                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else{
                            uploadImage();
                        }


                    } else {
                        Log.d("ProfileActivity", "No such document");
                    }
                } else {
                    Log.d("ProfileActivity", "get failed with ", task.getException());
                }
            }
        });

    }


    public void updateICUserVerify() {

        DocumentReference nameRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameRef
                .update("userPictureIC", icURL)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditName", "DocumentSnapshot successfully updated!");

                       /* Intent intent = new Intent(getApplicationContext(),customerProfile.class);
                        startActivity(intent);*/

                        Toast.makeText(customerVerifyAccount.this, "Document succesfully update.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });
    }

    /*public void updateSelfieUserVerify() {

        DocumentReference nameRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameRef
                .update("userSelfieWithIC", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditName", "DocumentSnapshot successfully updated!");

                       *//* Intent intent = new Intent(getApplicationContext(),customerProfile.class);
                        startActivity(intent);*//*

                        Toast.makeText(customerVerifyAccount.this, "Document succesfully update.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });
    }*/


    //method to convert bitmap to Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return res1 && res2;

    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void PickImage() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity().start(this);
    }

}