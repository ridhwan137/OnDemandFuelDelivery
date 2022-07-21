package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

public class AdminVerificationRequest extends AppCompatActivity implements VerificationRVAdapter.ItemClickListener {

    RecyclerView rvVerificationRequest;
    MaterialIconView mvCancel;

    ArrayList<User> userArrayList;
    VerificationRVAdapter verificationRVAdapter;
    FirebaseFirestore db;
    ProgressBar idProgressBar;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verification_request);


        // initializing our variables.
        rvVerificationRequest = findViewById(R.id.rvVerificationRequest);
        idProgressBar = findViewById(R.id.idProgressBar);

        mvCancel = findViewById(R.id.mvCancel);


        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        userArrayList = new ArrayList<>();
       // rvVerify.setHasFixedSize(true);
        rvVerificationRequest.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        verificationRVAdapter = new VerificationRVAdapter(userArrayList, this);

        // enable setclicklistener to recyclerview
        verificationRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvVerificationRequest.setAdapter(verificationRVAdapter);

        // below line is use to get the data from Firebase Firestore.
        // previously we were saving data on a reference of Courses
        // now we will be getting the data from the same reference.
        db.collection("users")
                .whereEqualTo("userType", "client")
                .whereEqualTo("accountStatus", "pending")
                .get()
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
                            idProgressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                User c = d.toObject(User.class);

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                userArrayList.add(c);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifuDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            verificationRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            idProgressBar.setVisibility(View.GONE);
                            /*tvEmptyDb.setVisibility(View.VISIBLE);*/
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        email = verificationRVAdapter.getItem(position).getEmail();
        Log.e("Check", "Email: " + email);
        //Toast.makeText(getApplicationContext()," Email: " + email, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(),AdminVerificationRequestDetail.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}