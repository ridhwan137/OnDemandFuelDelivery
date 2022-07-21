package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

public class ResetPassword extends AppCompatActivity {

    Button btnEmailResend;
    EditText emailResend;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MaterialIconView mvBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        btnEmailResend = findViewById(R.id.btnEmailResend);
        emailResend = findViewById(R.id.emailResend);
        mvBackBtn = findViewById(R.id.mvBackBtn);

        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnEmailResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExistingUser();
            }
        });

        mvBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public void checkExistingUser(){

        String email = emailResend.getText().toString().trim();

        if(!email.isEmpty()) {

            if(email.contains("@") && email.contains(".com")){

                Log.d("TAG", "Valid Email.");

                db.collection("users")
                        .whereEqualTo("Email", emailResend.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData());

                                        String user = document.getString("Email");

                                        if(user.equals(emailResend.getText().toString())){
                                            Log.d("TAG", "User Exists");

                                            // if the user is exist in database, then password
                                            // reset will be send to the user's email
                                            sendPasswordReset();

                                        }
                                    }
                                }
                                if(task.getResult().size() == 0 ) {
                                    Log.d("TAG", "User not Exists");

                                    Toast.makeText(ResetPassword.this, "User does not exist!", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

            }
            else{

                Toast.makeText(ResetPassword.this, "Invalid email format.", Toast.LENGTH_LONG).show();

            }

        }
        else{

            Toast.makeText(ResetPassword.this, "Please enter your email first before submit.", Toast.LENGTH_LONG).show();

        }


    }

    public void sendPasswordReset() {

        String emailAddress = emailResend.getText().toString().trim();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Email sent.");
                            Toast.makeText(ResetPassword.this, "Email sent.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}