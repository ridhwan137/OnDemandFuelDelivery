package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.HashMap;
import java.util.Map;


public class editCustomerProfile extends AppCompatActivity {

    private static final String TAG = "UserProfile";

    LinearLayout lytCustomerName, lytCustomerAddress, lytCustomerPassword, lytCustomerMobileNum;

    TextView tvName, tvPhone, tvAddress, tvPassword;

    MaterialIconView mvCancel;

    FirebaseFirestore db;

    Map<String,Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_profile);


        // Text View variables for name, address,phonenum.
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPassword = findViewById(R.id.tvPassword);

        lytCustomerName = findViewById(R.id.lytCustomerName);
        lytCustomerAddress = findViewById(R.id.lytCustomerAddress);
        lytCustomerPassword = findViewById(R.id.lytCustomerPassword);
        lytCustomerMobileNum = findViewById(R.id.lytCustomerMobileNum);

        mvCancel = findViewById(R.id.mvCancel);

        //create firebase firestore instance
        db = FirebaseFirestore.getInstance();

        //Display all the customer details from the Firebase
        //fx call
        displayUserProfile();

        lytCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), editNameCustomerProfile.class);
                startActivity(intent);
            }
        });

        lytCustomerAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), editAddressCustomerProfile.class);
                startActivity(intent);
            }
        });

        lytCustomerPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), editPasswordCustomerProfile.class);
                startActivity(intent);
            }
        });

        lytCustomerMobileNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), editPhoneNumCustomerProfile.class);
                startActivity(intent);
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editCustomerProfile.this, customerProfile.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }


}