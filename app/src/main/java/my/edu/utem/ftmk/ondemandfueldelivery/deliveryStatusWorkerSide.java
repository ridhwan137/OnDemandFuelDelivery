package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class deliveryStatusWorkerSide extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> ordercreated = new HashMap<>();
    String statusorder;
    private Date date;
    String waypointID;

    Button btnWorkerUpdateOrderDelivered;

    TextView txtDateOrderCreated, txtTimeOrderCreated, txtCustomerNameOrderCreated, txtCustomerMobileOrderCreated, txtWorkerName, txtPetronStation,
            txtOrderNum, txtRiderPlateNum, txtDateOrderAccepted, txtTimeOrderAccepted,
            txtTimeETA, txtAddressCustomerOrderCreated;

    LinearLayout lytOrderAccepted, lytOrderAcceptedDetails, lytOnDelivery, lytOnDeliveryDetails, lytOrderDetails, lytEmptyOrder;

    ImageView profileNavigation, mapNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status_worker_side);


        txtDateOrderCreated = findViewById(R.id.txtDateOrderCreated);
        txtTimeOrderCreated = findViewById(R.id.txtTimeOrderCreated);
        txtCustomerNameOrderCreated = findViewById(R.id.txtCustomerNameOrderCreated);
        txtCustomerMobileOrderCreated = findViewById(R.id.txtCustomerMobileOrderCreated);
        txtWorkerName = findViewById(R.id.txtWorkerName);
        txtPetronStation = findViewById(R.id.txtPetronStation);
        txtOrderNum = findViewById(R.id.txtOrderNum);
        txtRiderPlateNum = findViewById(R.id.txtRiderPlateNum);
        txtDateOrderAccepted = findViewById(R.id.txtDateOrderAccepted);
        txtTimeOrderAccepted = findViewById(R.id.txtTimeOrderAccepted);
        //txtTimeETA = findViewById(R.id.txtTimeETA);
        txtAddressCustomerOrderCreated = findViewById(R.id.txtAddressCustomerOrderCreated);

        lytOrderAccepted = findViewById(R.id.lytOrderAccepted);
        lytOrderAcceptedDetails = findViewById(R.id.lytOrderAcceptedDetails);
        lytOnDelivery = findViewById(R.id.lytOnDelivery);
        lytOnDeliveryDetails = findViewById(R.id.lytOnDeliveryDetails);

        lytOrderDetails = findViewById(R.id.lytOrderDetails);
        lytEmptyOrder = findViewById(R.id.lytEmptyOrder);

        btnWorkerUpdateOrderDelivered = findViewById(R.id.btnWorkerUpdateOrderDelivered);

        /*profileNavigation = findViewById(R.id.profileNavigation);
        mapNavigation = findViewById(R.id.mapNavigation);*/


        db = FirebaseFirestore.getInstance();

        //initialize and assign variable bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //set Map Selected
        bottomNavigationView.setSelectedItemId(R.id.deliveryNavigation);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.deliveryNavigation:

                        return true;

                    case R.id.mapNavigation:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profileNavigation:
                        startActivity(new Intent(getApplicationContext(), workerProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", "ondelivery")
                .whereNotEqualTo("status", "complete")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {

                            if(task.getResult().getDocuments().get(0).get("status").toString().equals("ondelivery"))
                            {
                                fxgetDate();
                                fxgetDateOrderAccept();
                                fxGetOrderAcceptedDetails();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_SHORT).show();
                                lytOrderDetails.setVisibility(View.INVISIBLE);
                                lytEmptyOrder.setVisibility(View.VISIBLE);
                            }


                        } else {

                            lytOrderDetails.setVisibility(View.INVISIBLE);
                             lytEmptyOrder.setVisibility(View.VISIBLE);

                        }
                    }
                });

        btnWorkerUpdateOrderDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order Complete. Thank You", Toast.LENGTH_SHORT).show();
                updateOrderDelivered();
                updateStatusOnWaypoint();


            }
        });







      /*  FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", "complete")
                .whereEqualTo("deliveryStatus", "delivered")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                            startActivity(intent);
                        }
                    }
                });*/


    }

    public void updateOrderDelivered(){

        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("orderStatus", "active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {

                            if(task.getResult().getDocuments().get(0).get("orderStatus").toString().equals("active"))
                            {
                                String waypointid = task.getResult().getDocuments().get(0).get("waypointid").toString();
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("order").document(waypointid);
                                documentReference
                                        .update("orderStatus", "unactive")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d(TAG, "DocumentSnapshot successfully updated!");To
                                                //Toast.makeText(deliveryStatusWorkerSide.this, "Succesful Update", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(deliveryStatusWorkerSide.this, "Unsuccesful Update", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }


                        }
                    }
                });


    }

    public void updateStatusOnWaypoint(){
        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereNotEqualTo("status", "complete")
                .whereEqualTo("status", "ondelivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {

                            if(task.getResult().getDocuments().get(0).get("status").toString().equals("ondelivery"))
                            {
                                String waypointid = task.getResult().getDocuments().get(0).get("waypointid").toString();
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("waypoint").document(waypointid);
                                documentReference
                                        .update("status", "complete")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d(TAG, "DocumentSnapshot successfully updated!");To
                                                //Toast.makeText(deliveryStatusWorkerSide.this, "Succesful Update On Waypoint", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Toast.makeText(deliveryStatusWorkerSide.this, "Unsuccesful Update On Waypoint", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }


                        }
                    }
                });



    }

    public void fxDisplayOrderCreated() {


        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        txtDateOrderCreated.setText(document.getData().get("FullName").toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


    }

    public void fxgetDate() {


        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Timestamp firebaseTimestampObject = document.getTimestamp("date");
                                Date javaDate = firebaseTimestampObject.toDate();

                                /*Timestamp date;
                                date = document.getData().get("date");
                                sfd.format(new Date(date));*/

                                txtDateOrderCreated.setText(getDate(javaDate));
                                txtTimeOrderCreated.setText(getTime(javaDate));


                            }
                        } else {

                        }
                    }
                });


    }

    public void fxgetDateOrderAccept(){
        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereNotEqualTo("orderStatus", "unactive")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Timestamp firebaseTimestampObject = document.getTimestamp("date");
                                Date javaDate = firebaseTimestampObject.toDate();

                                /*Timestamp date;
                                date = document.getData().get("date");
                                sfd.format(new Date(date));*/

                                txtDateOrderAccepted.setText(getDate(javaDate));
                                txtTimeOrderAccepted.setText(getTime(javaDate));


                                Log.e("est date", String.valueOf(getTimeEST(javaDate)));


                            }
                        } else {

                        }
                    }
                });
    }

    String getDate(Date javaDate) {
        /* Calendar cal = Calendar.getInstance();*/

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String localDate = sdf.format(javaDate);
        Log.d("tngok tarikh", localDate);

        return localDate;
    }

    String getTime(Date javaDate) {
        /* Calendar cal = Calendar.getInstance();*/

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String localTime = sdf.format(javaDate);

        return localTime;
    }

    public int getTimeEST(Date javaDate) {
        /* Calendar cal = Calendar.getInstance();*/
        int estTime = 0;

        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm");

        String localTimeEst = sdf2.format(javaDate);


        /*estTime = Integer.parseInt(localTimeEst) + 15;
        Log.e("Test Date EST", String.valueOf(estTime)); // output = 25
        */

        return estTime;

    }


    public void fxGetOrderAcceptedDetails() {
        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                txtWorkerName.setText(document.getData().get("workername").toString());
                                txtPetronStation.setText(document.getData().get("petrolstation").toString());
                                txtOrderNum.setText(document.getData().get("orderNumber").toString());
                                txtRiderPlateNum.setText(document.getData().get("plateNumber").toString());
                                txtCustomerNameOrderCreated.setText(document.getData().get("FullName").toString());
                                txtCustomerMobileOrderCreated.setText(document.getData().get("PhoneNum").toString());
                                txtAddressCustomerOrderCreated.setText(document.getData().get("addresslatlng").toString());



                            }
                        } else {

                        }
                    }
                });


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}

