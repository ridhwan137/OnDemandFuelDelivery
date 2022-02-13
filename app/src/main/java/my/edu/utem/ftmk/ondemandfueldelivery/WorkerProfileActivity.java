package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfileActivity extends AppCompatActivity {

    private static final String TAG = "WorkerProfile";
    private RecyclerView rvRecord;
    private ArrayList<Record> recordArrayList;
    private RecordRVAdapter recordRVAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;

    private LinearLayout editProfileLayout;
    private TextView tvName, tvPhone, tvAddress, tvEdit, txtEmailUser;
    private EditText etName, etPhone, etAddress;
    private MaterialIconView mvConfirm, mvCancel, mvLogout;

    private double latitude, longitude;
    private Geocoder geocoder;
    List<Address> address;
    GeoPoint geoPoint;

    Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        // initializing our variables.

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvEdit = findViewById(R.id.tvEdit);
        txtEmailUser =findViewById(R.id.txtEmailUser);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        editProfileLayout = findViewById(R.id.editProfileLayout);

        mvConfirm = findViewById(R.id.mvConfirm);
        mvCancel = findViewById(R.id.mvCancel);
        mvLogout = findViewById(R.id.mvLogout);

        rvRecord = findViewById(R.id.rvRecord);
        loadingPB = findViewById(R.id.idProgressBar);

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();






        // below line is use to get the data from Firebase Firestore.
        // previously we were saving data on a reference of Courses
        // now we will be getting the data from the same reference.
        db.collection("waypoint").whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are
                            // hiding our progress bar and adding
                            // our data in a list.
                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Record c = d.toObject(Record.class);

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                recordArrayList.add(c);

                                Log.e("TAG", "test: " + c);
                            }

                            // after adding the data to recycler view.
                            // we are calling recycler view notifyDataSetChanged
                            // method to notify that data has been changed in recycler view.

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(WorkerProfileActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        displayUserProfile();
        showUserDataToBeUpdated();

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileLayout.setVisibility(View.VISIBLE);
            }
        });

        mvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUserProfile();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileLayout.setVisibility(View.INVISIBLE);
            }
        });

        mvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerProfileActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Logout Confirmation");
                dialog.setMessage("Are you sure you want to logout?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Logout".
                        logoutUser();
                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".

                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
    }

    public void displayUserProfile(){

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        txtEmailUser.setText(document.getData().get("Email").toString());
                        tvName.setText(document.getData().get("FullName").toString());
                        tvPhone.setText(document.getData().get("PhoneNum").toString());
                        tvAddress.setText(document.getData().get("Address").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void showUserDataToBeUpdated(){

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        etName.setText(document.getData().get("FullName").toString());
                        etPhone.setText(document.getData().get("PhoneNum").toString());
                        etAddress.setText(document.getData().get("Address").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void EditUserProfile(){

        user.put("name",etName.getText().toString());
        user.put("phone",etPhone.getText().toString());
        user.put("address",etAddress.getText().toString());

//        if(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
//
//            user.put("password",etConfirmPassword.getText().toString());

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef
                .update("FullName", user.get("name"),"PhoneNum",user.get("phone"), "Address", user.get("address"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        Toast.makeText(WorkerProfileActivity.this, "Your profile has been updated.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(WorkerProfileActivity.this, WorkerProfileActivity.class);
        startActivity(intent);

        editProfileLayout.setVisibility(View.INVISIBLE);

//        else {
//            Toast.makeText(UserProfile.this, "Password is not match.", Toast.LENGTH_SHORT).show();
//            showUserDataToBeUpdated();
//        }
    }

    public void toMap(View v){

        Intent intent = new Intent(WorkerProfileActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void logoutUser(){

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(WorkerProfileActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}