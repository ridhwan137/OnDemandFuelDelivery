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


public class editNameCustomerProfile extends AppCompatActivity {

    FirebaseFirestore db;
    Map<String, Object> user = new HashMap<>();
    TextView etEditName;
    Button btnSave;
    MaterialIconView mvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name_client_profile);

        etEditName = findViewById(R.id.etEditName);
        btnSave = findViewById(R.id.btnSave);

        mvCancel = findViewById(R.id.mvCancel);



        db = FirebaseFirestore.getInstance();

        displayCustomerName();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCustomerName();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editNameCustomerProfile.this, editCustomerProfile.class);
                startActivity(intent);
            }
        });
    }

    public void displayCustomerName()
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
                        Log.d("nak tngok nama customer", "DocumentSnapshot data: " + doc.getData());
                        etEditName.setText(doc.getData().get("FullName").toString());

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Failed to get your name", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d("Failed", "get failed with ", task.getException());
                }
            }
        });
    }

    public void updateCustomerName(){

        user.put("updateName", etEditName.getText().toString());

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.update("FullName", user.get("updateName"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Toast.makeText(getApplicationContext(), "Succesfull Update Your Name", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed To Update Your Name", Toast.LENGTH_SHORT).show();
                    }
                });

            Intent intent = new Intent(editNameCustomerProfile.this, editCustomerProfile.class);
            startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }


}