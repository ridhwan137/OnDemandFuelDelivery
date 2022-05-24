package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.HashMap;
import java.util.Map;


public class editPasswordCustomerProfile extends AppCompatActivity {

    FirebaseFirestore db;
    TextView etEditCurrentPassword, etEditNewPassword;
    Button btnSave;
    MaterialIconView mvCancel;


    Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password_client_profile);

        etEditCurrentPassword = findViewById(R.id.etEditCurrentPassword);
        etEditNewPassword = findViewById(R.id.etEditNewPassword);
        btnSave = findViewById(R.id.btnSave);
        mvCancel = findViewById(R.id.mvCancel);

        db = FirebaseFirestore.getInstance();

        checkCustomerCurrentPasssword();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateCustomerPassword();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editPasswordCustomerProfile.this, editCustomerProfile.class);
                startActivity(intent);
            }
        });


    }

    public void checkCustomerCurrentPasssword()
    {
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists())
                    {
                        Log.d("nak tngok current password customer", "DocumentSnapshot data: " + doc.getData());
                        String currentPassword = doc.getData().get("Password").toString();
                        user.put("currentPassword",currentPassword);
                        Log.e("currentpassword", currentPassword);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Failed to get your current password", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d("Failed", "get failed with ", task.getException());
                }
            }
        });
    }


    public void updateCustomerPassword(){

        user.put("updatePassword", etEditNewPassword.getText().toString());

        checkCustomerCurrentPasssword();

        if (etEditCurrentPassword.getText().toString().equals(user.get("currentPassword")))
        {
            DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            docRef.update("Password", user.get("updatePassword"))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            Log.d("Password Updated", "DocumentSnapshot successfully updated!");
                            Toast.makeText(getApplicationContext(), "Succesfull Update Your Password", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Password Failed to Update", "Error Updatating Doc!");
                            Toast.makeText(getApplicationContext(), "Failed To Update Your New Password", Toast.LENGTH_SHORT).show();
                        }
                    });


            Intent intent = new Intent(editPasswordCustomerProfile.this, editCustomerProfile.class);
            startActivity(intent);

        }
        else
        {
            Toast.makeText(editPasswordCustomerProfile.this, "Password Is Not Match. Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }


}