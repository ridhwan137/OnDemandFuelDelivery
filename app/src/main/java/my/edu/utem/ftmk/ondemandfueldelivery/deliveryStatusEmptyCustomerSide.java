package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class deliveryStatusEmptyCustomerSide extends AppCompatActivity {

    String userType;
    ImageView profileNavigation, mapNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status_empty_customer_side);

        profileNavigation = findViewById(R.id.profileNavigation);
        mapNavigation = findViewById(R.id.mapNavigation);

        //navigate to user profile
        profileNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(deliveryStatusEmptyCustomerSide.this, customerProfile.class);
                startActivity(intent);
            }
        });

        //navigate to map
        mapNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(deliveryStatusEmptyCustomerSide.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }

    public void toUserProfile(View v){

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        userType = document.getData().get("userType").toString();
                        Log.e("kapiaq user 2", userType);
                        if (userType.equals("client")) {
                            Intent intent = new Intent(deliveryStatusEmptyCustomerSide.this, UserProfile.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(deliveryStatusEmptyCustomerSide.this, WorkerProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Log.e("tk dak pa", "tak dak");
                    }

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