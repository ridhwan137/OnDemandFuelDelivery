package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialIconView;

public class AdminVerificationRequestDetail extends AppCompatActivity {

    ImageView ivIC, ivSelfieWithIC;
    TextView txtFullNameUser, txtEmailUser, txtMobileNumberUser;
    Button btnApproveVerification, btnRejectVerification;
    MaterialIconView mvCancel;

    String email, icURL, selfieURL, userid;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verification_request_detail);

        ivIC = findViewById(R.id.ivIC);
        ivSelfieWithIC = findViewById(R.id.ivSelfieWithIC);

        txtFullNameUser = findViewById(R.id.txtFullNameUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        txtMobileNumberUser = findViewById(R.id.txtMobileNumberUser);

        btnApproveVerification = findViewById(R.id.btnApproveVerification);
        btnRejectVerification = findViewById(R.id.btnRejectVerification);

        mvCancel = findViewById(R.id.mvCancel);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        displayUserDetails();

        btnApproveVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptUserRequest();
            }
        });

        btnRejectVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectUserRequest();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminVerificationRequest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



    }

    public void displayUserDetails(){
        db.collection("users")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                icURL = document.getData().get("userPictureIC").toString();
                                selfieURL = document.getData().get("userSelfieWithIC").toString();
                                userid = document.getId();
                                Log.e("Tngok UserID", userid);


                                Picasso.with(getApplicationContext()).load(icURL).into(ivIC);
                                Picasso.with(getApplicationContext()).load(selfieURL).into(ivSelfieWithIC);


                                txtFullNameUser.setText(document.getData().get("FullName").toString());
                                txtEmailUser.setText(document.getData().get("Email").toString());
                                txtMobileNumberUser.setText(document.getData().get("PhoneNum").toString());

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void acceptUserRequest(){

        DocumentReference docRef = db.collection("users").document(userid);

        docRef
                .update("accountStatus", "verified")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent intent = new Intent(getApplicationContext(), AdminVerificationRequest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "The account have been succesfuly verified", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });

    }

    public void rejectUserRequest(){
        DocumentReference docRef = db.collection("users").document(userid);

        docRef
                .update("accountStatus", "rejected")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent intent = new Intent(getApplicationContext(), AdminVerificationRequest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "The account have been succesfuly rejected", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });
    }


    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}