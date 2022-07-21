package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.HashMap;
import java.util.Map;

public class AdminRegisterWorkerAccount extends AppCompatActivity {

    EditText etEmail, etFullName, etPassword, etAddress, etPhoneNum , etPetrolStation, etRiderPlateNumber;
    MaterialIconView mvCancel;
    Button btSignUp;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_worker_account);

        //declare EditText
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNum = findViewById(R.id.etPhoneNum);
        etPetrolStation = findViewById(R.id.etPetrolStation);
        etRiderPlateNumber = findViewById(R.id.etRiderPlateNumber);

        mvCancel = findViewById(R.id.mvCancel);

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //Declare Button Sign Up
        btSignUp = findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUserWorker();
            }
        });



    }

    public void registerUserWorker()
    {
        // Create a new user with a first and last name

        user.put("Email", etEmail.getText().toString());
        user.put("Password", etPassword.getText().toString());
        user.put("FullName", etFullName.getText().toString());
        user.put("PhoneNum", etPhoneNum.getText().toString());
        user.put("Address", etAddress.getText().toString());
        user.put("petrolstation", etPetrolStation.getText().toString());
        user.put("plateNumber", etRiderPlateNumber.getText().toString());
        user.put("Picture URL", "-");
        user.put("userType", "worker");

        if(!etEmail.getText().toString().equals("") && !etFullName.getText().toString().equals("") && !etPhoneNum.getText().toString().equals("")
                && !etPassword.getText().toString().equals("") && !etAddress.getText().toString().equals("")) {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(@NonNull AuthResult authResult) {
                            // Register User add to Firebase
                            firebaseFirestore.collection("users")
                                    .document(authResult.getUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            Toast.makeText(getApplicationContext(), "Sign Up Succesfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                            startActivity(intent);
                                        }
                                    });


                        }
                    });
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Sign Up Failed", Toast.LENGTH_LONG).show();
        }

    }
}