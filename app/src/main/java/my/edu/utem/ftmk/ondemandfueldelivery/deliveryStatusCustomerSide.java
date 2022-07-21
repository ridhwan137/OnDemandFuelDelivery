package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class deliveryStatusCustomerSide extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> ordercreated = new HashMap<>();
    String statusorder;
    private Date date;

    TextView txtDateOrderCreated, txtTimeOrderCreated, txtWorkerName, txtPetronStation,
            txtOrderNum, txtRiderPlateNum, txtDateOrderAccepted, txtTimeOrderAccepted,
            txtTimeETA;

    Button btnCancelOrder;

    LinearLayout lytOrderAccepted, lytOrderAcceptedDetails, lytOnDelivery, lytOnDeliveryDetails ,lytOrderEmpty, lytMainOrderCreated, lytTellUser;

    ImageView profileNavigation, mapNavigation;
    String waypointid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status_customer_side);


        txtDateOrderCreated = findViewById(R.id.txtDateOrderCreated);
        txtTimeOrderCreated = findViewById(R.id.txtTimeOrderCreated);
        txtWorkerName = findViewById(R.id.txtWorkerName);
        txtPetronStation = findViewById(R.id.txtPetronStation);
        txtOrderNum = findViewById(R.id.txtOrderNum);
        txtRiderPlateNum = findViewById(R.id.txtRiderPlateNum);
        txtDateOrderAccepted = findViewById(R.id.txtDateOrderAccepted);
        txtTimeOrderAccepted = findViewById(R.id.txtTimeOrderAccepted);
        //txtTimeETA = findViewById(R.id.txtTimeETA);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        lytOrderAccepted = findViewById(R.id.lytOrderAccepted);
        lytOrderAcceptedDetails = findViewById(R.id.lytOrderAcceptedDetails);
        lytOnDelivery = findViewById(R.id.lytOnDelivery);
        lytOnDeliveryDetails = findViewById(R.id.lytOnDeliveryDetails);
        lytOrderEmpty = findViewById(R.id.lytOrderEmpty);
        lytMainOrderCreated = findViewById(R.id.lytMainOrderCreated);
        lytTellUser = findViewById(R.id.lytTellUser);

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
                        startActivity(new Intent(getApplicationContext(), customerProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

      /*  //navigate to user profile
        profileNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(deliveryStatusCustomerSide.this, customerProfile.class);
                startActivity(intent);
            }
        });

        //navigate to map
        mapNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(deliveryStatusCustomerSide.this, MapActivity.class);
                startActivity(intent);
            }
        });*/


   /*     DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {
                        Log.d("nak tngok nama customer", "DocumentSnapshot data: " + doc.getData());
                        statusorder = "pending";

                        Log.e("Nak Tngok Nama User Login", String.valueOf(doc.getData().get("FullName")));

                        FirebaseFirestore.getInstance().collection("waypoint")
                                .whereEqualTo("status", "pending")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {


                                            FirebaseFirestore.getInstance().collection("waypoint").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                                    if (!value.isEmpty() && statusorder.equals("pending")) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {


                                                        }

                                                    } else {
                                                        Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                                                        startActivity(intent);
                                                    }

                                                }
                                            });

                                        } else {

                                        }
                                    }
                                });


                    } else {
                        Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                        startActivity(intent);
                    }
                } else {
                    Log.d("Failed", "get failed with ", task.getException());
                }
            }
        });*/


/*

        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", "delivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance().collection("waypoint").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                    if (!value.isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            lytOrderAccepted.setVisibility(View.VISIBLE);
                                            lytOrderAcceptedDetails.setVisibility(View.VISIBLE);
                                            lytOnDelivery.setVisibility(View.VISIBLE);
                                            lytOnDeliveryDetails.setVisibility(View.VISIBLE);
                                            fxgetDate();
                                            fxGetOrderAcceptedDetails();
                                        }

                                    } else {
                                        Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                                        startActivity(intent);
                                    }

                                }
                            });
                        } else {

                            Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                            startActivity(intent);
                        }
                    }
                });
*/

       /* FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", "delivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance().collection("waypoint").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                    if (!value.isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            lytOrderAccepted.setVisibility(View.VISIBLE);
                                            lytOrderAcceptedDetails.setVisibility(View.VISIBLE);
                                            lytOnDelivery.setVisibility(View.VISIBLE);
                                            lytOnDeliveryDetails.setVisibility(View.VISIBLE);
                                            fxgetDate();
                                            fxGetOrderAcceptedDetails();
                                        }

                                    } else {
                                        Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                                        startActivity(intent);
                                    }

                                }
                            });
                        } else {

                            Intent intent = new Intent(deliveryStatusCustomerSide.this, deliveryStatusEmptyCustomerSide.class);
                            startActivity(intent);
                        }
                    }
                });*/

        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereNotEqualTo("status", "complete")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {

                            if(task.getResult().getDocuments().get(0).get("status").toString().equals("pending"))
                            {
                                lytOrderAccepted.setVisibility(View.INVISIBLE);
                                lytOrderAcceptedDetails.setVisibility(View.INVISIBLE);
                                lytOnDelivery.setVisibility(View.INVISIBLE);
                                lytOnDeliveryDetails.setVisibility(View.INVISIBLE);
                                lytMainOrderCreated.setVisibility(View.VISIBLE);
                                lytTellUser.setVisibility(View.VISIBLE);
                                fxgetDate();
                                waypointid = task.getResult().getDocuments().get(0).getData().get("waypointid").toString();
                                //Toast.makeText(getApplicationContext(), waypointid, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                lytOrderAccepted.setVisibility(View.VISIBLE);
                                lytOrderAcceptedDetails.setVisibility(View.VISIBLE);
                                lytOnDelivery.setVisibility(View.VISIBLE);
                                lytOnDeliveryDetails.setVisibility(View.VISIBLE);
                                lytMainOrderCreated.setVisibility(View.VISIBLE);
                                lytTellUser.setVisibility(View.INVISIBLE);
                                fxgetDate();
                                fxgetDateOrderAccept();
                                fxGetOrderAcceptedDetails();
                            }



                        } else {

                            lytOrderEmpty.setVisibility(View.VISIBLE);


                        }
                    }
                });

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(deliveryStatusCustomerSide.this);
                dialog.setCancelable(false);
                dialog.setTitle("Cancel The Order");
                dialog.setMessage("Are you sure you want to cancel your order?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Logout".
                        cancelOrder();
                        lytTellUser.setVisibility(View.INVISIBLE);
                        lytOrderEmpty.setVisibility(View.VISIBLE);
                        lytOrderAccepted.setVisibility(View.INVISIBLE);
                        lytOrderAcceptedDetails.setVisibility(View.INVISIBLE);
                        lytOnDelivery.setVisibility(View.INVISIBLE);
                        lytOnDeliveryDetails.setVisibility(View.INVISIBLE);
                        lytMainOrderCreated.setVisibility(View.INVISIBLE);
                    }
                })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".

                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.bakmaiyellow));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));



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
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereNotEqualTo("status", "complete")
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
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
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

    public void cancelOrder(){
        db.collection("waypoint").document(waypointid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Succesfully cancel the order", Toast.LENGTH_SHORT).show();
                        Log.d("Order Delete", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Order Tak Delete", "Error deleting document", e);
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
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
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

