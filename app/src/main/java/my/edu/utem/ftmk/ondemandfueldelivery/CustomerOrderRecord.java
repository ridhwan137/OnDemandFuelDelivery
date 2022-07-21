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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderRecord extends AppCompatActivity implements CustomerOrderRVAdapter.ItemClickListener {

    private RecyclerView rvOrder;
    MaterialIconView mvCancel;
    private ArrayList<Order> orderArrayList;
    private CustomerOrderRVAdapter customerOrderRVAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_record);

        // initializing our variables.
        rvOrder = findViewById(R.id.rvOrder);
        loadingPB = findViewById(R.id.idProgressBar);

        mvCancel = findViewById(R.id.mvCancel);



        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        orderArrayList = new ArrayList<>();
        rvOrder.setHasFixedSize(true);
        rvOrder.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        customerOrderRVAdapter = new CustomerOrderRVAdapter(orderArrayList, this);

        // enable setclicklistener to recyclerview


        // setting adapter to our recycler view.
        rvOrder.setAdapter(customerOrderRVAdapter);


        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        userType = document.getData().get("userType").toString();

                        Log.e("kapiaq user", userType);
                        if (userType.equals("client")) {

                            mvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), customerProfile.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });

                            // below line is use to get the data from Firebase Firestore.
                            // previously we were saving data on a reference of Courses
                            // now we will be getting the data from the same reference.
                            db.collection("order")
                                    .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                                loadingPB.setVisibility(View.GONE);
                                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot d : list) {
                                                    // after getting this list we are passing
                                                    // that list to our object class.
                                                    Order c = d.toObject(Order.class);

                                                    // and we will pass this object class
                                                    // inside our arraylist which we have
                                                    // created for recycler view.
                                                    orderArrayList.add(c);
                                                }
                                                // after adding the data to recycler view.
                                                // we are calling recycler view notifuDataSetChanged
                                                // method to notify that data has been changed in recycler view.
                                                customerOrderRVAdapter.notifyDataSetChanged();
                                            } else {
                                                // if the snapshot is empty we are displaying a toast message.
                                                loadingPB.setVisibility(View.GONE);

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

                        } else if (userType.equals("worker")) {

                            mvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), workerProfile.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });

                            // below line is use to get the data from Firebase Firestore.
                            // previously we were saving data on a reference of Courses
                            // now we will be getting the data from the same reference.
                            db.collection("order")
                                    .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                                loadingPB.setVisibility(View.GONE);
                                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot d : list) {
                                                    // after getting this list we are passing
                                                    // that list to our object class.
                                                    Order c = d.toObject(Order.class);

                                                    // and we will pass this object class
                                                    // inside our arraylist which we have
                                                    // created for recycler view.
                                                    orderArrayList.add(c);
                                                }
                                                // after adding the data to recycler view.
                                                // we are calling recycler view notifuDataSetChanged
                                                // method to notify that data has been changed in recycler view.
                                                customerOrderRVAdapter.notifyDataSetChanged();
                                            } else {
                                                // if the snapshot is empty we are displaying a toast message.
                                                loadingPB.setVisibility(View.GONE);

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

                        }
                    }
                }
            }
        });






    }

    @Override
    public void onItemClick(View view, int position) {


    }
}