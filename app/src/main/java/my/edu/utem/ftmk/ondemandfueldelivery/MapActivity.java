package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    FloatingActionButton btnAutoLocate;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;

    double userLatitude, userLongitude;
    String typeOfFuel;
    Double prices = 10.00, fuelLitre;
    Map<Marker, Map<String, Object>> markerData = new HashMap<>();

    String userType;
    String waypointID;


    TextView txtPrice, txtRon95, txtRon97, txtDiesel, txtUsername, txtAddressUser, txtTypeFuelUser, txtPriceFuelUser;
    SeekBar sliderPrice;
    CardView cvRon95, cvRon97, cvDiesel;
    Button btnRequest, btnRequestMenu, btnAcceptUserRequest;
    LinearLayout layoutRequestFuel, layoutFake, layoutRequestFuelUserDetail;
    List<Address> addresslatlng;
    Geocoder geocoder;

    Map<String, Object> user = new HashMap<>();
    Map<String, Object> waypoint = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Date date = new Date();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());

        btnAutoLocate = findViewById(R.id.btnAutoLocate);
        btnRequest = findViewById(R.id.btnRequest);
        btnRequestMenu = findViewById(R.id.btnRequestMenu);
        btnAcceptUserRequest = findViewById(R.id.btnAcceptUserRequest);

        sliderPrice = findViewById(R.id.sliderPrice);
        txtPrice = findViewById(R.id.txtPrice);

        cvRon95 = findViewById(R.id.cVRon95);
        cvRon97 = findViewById(R.id.cVRon97);
        cvDiesel = findViewById(R.id.cVDiesel);

        txtRon95 = findViewById(R.id.txtPriceRon95);
        txtRon97 = findViewById(R.id.txtPriceRon97);
        txtDiesel = findViewById(R.id.txtDiesel);

        txtUsername = findViewById(R.id.txtUsername);
        txtAddressUser = findViewById(R.id.txtAddressUser);
        txtTypeFuelUser = findViewById(R.id.txtTypeFuelUser);
        txtPriceFuelUser = findViewById(R.id.txtPriceFuelUser);


        layoutRequestFuel = findViewById(R.id.layoutRequestFuel);
        layoutRequestFuelUserDetail = findViewById(R.id.layoutRequestFuelUserDetail);
        layoutFake = findViewById(R.id.layoutFake);

       // fxCheckUserType();
        //Log.e("test login user type", userType);
       // if (userType.equals("client"))
        //{

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
                        if (userType.equals("client"))
                        {

                            btnAcceptUserRequest.setVisibility(View.INVISIBLE);

                            btnRequestMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutFake.setVisibility(View.VISIBLE);
                                    layoutRequestFuel.setVisibility(View.VISIBLE);
                                }
                            });

                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("fuel").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (!value.isEmpty()) {
                                        for (DocumentSnapshot documentSnapshot : value) {
                                            if (documentSnapshot.getString("name").equals("ron95")) {
                                                txtRon95.setText("RM" + documentSnapshot.getString("price") + "/L");
                                            } else if (documentSnapshot.getString("name").equals("ron97")) {
                                                txtRon97.setText("RM" + documentSnapshot.getString("price") + "/L");
                                            } else if (documentSnapshot.getString("name").equals("diesel")) {
                                                txtDiesel.setText("RM" + documentSnapshot.getString("price") + "/L");
                                            }
                                        }
                                    }
                                }
                            });


                            sliderPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    int step = 5;
                                    i = ((int) Math.round(i / step)) * step;
                                    seekBar.setProgress(i);
                                    prices = (double) i;
                                    txtPrice.setText("RM" + String.valueOf(i) + ".00");
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                            cvRon95.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cvRon95.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cvRon97.setCardBackgroundColor(Color.WHITE);
                                    cvDiesel.setCardBackgroundColor(Color.WHITE);
                                    typeOfFuel = "RON95";
                                }
                            });

                            cvRon97.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cvRon97.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cvRon95.setCardBackgroundColor(Color.WHITE);
                                    cvDiesel.setCardBackgroundColor(Color.WHITE);
                                    typeOfFuel = "RON97";
                                }
                            });

                            cvDiesel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cvDiesel.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cvRon95.setCardBackgroundColor(Color.WHITE);
                                    cvRon97.setCardBackgroundColor(Color.WHITE);
                                    typeOfFuel = "DIESEL";
                                }
                            });

                            btnRequest.setOnClickListener(new View.OnClickListener() {


                                @Override
                                public void onClick(View view) {


                                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                                            //user = documentSnapshot.getData();

                                            try {
                                                addresslatlng = geocoder.getFromLocation(userLatitude, userLongitude, 1);
                                            } catch (IOException exception) {
                                                exception.printStackTrace();
                                            }

                                            String addresswaypoint = addresslatlng.get(0).getAddressLine(0);

                                            user.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            user.put("price", String.format("%.2f", prices));
                                            user.put("Latitude", userLatitude);
                                            user.put("Longitude", userLongitude);
                                            user.put("typeoffuel", typeOfFuel);
                                            user.put("addresslatlng", addresswaypoint);
                                            user.put("status", "pending");
                                            user.put("date", date);

                                            //checkUserfx();
                                            FirebaseFirestore.getInstance().collection("waypoint")
                                                    .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .whereEqualTo("status", "pending")
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                                            Log.e("kk", String.valueOf(queryDocumentSnapshots.size()));
                                                            if (queryDocumentSnapshots.size() == 0 ) {
                                                                FirebaseFirestore.getInstance().collection("waypoint").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull DocumentReference documentReference) {
                                                                        Toast.makeText(MapActivity.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                                                                        Log.e("Noh Test", user.toString());
                                                                        layoutRequestFuel.setVisibility(View.INVISIBLE);
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(MapActivity.this, "Your Request Are Pending", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        }
                                    });

                                }

                            });

                            layoutFake.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutFake.setVisibility(View.INVISIBLE);
                                    layoutRequestFuel.setVisibility(View.INVISIBLE);
                                    layoutRequestFuelUserDetail.setVisibility(View.INVISIBLE);
                                }
                            });

                            layoutRequestFuel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });


                            checkMyPermission();
                            initMap();
                            mLocationClient = new FusedLocationProviderClient(MapActivity.this);

                            btnAutoLocate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getCurrLoc();
                                }
                            });




                        }
                        else
                        {
                            btnRequestMenu.setVisibility(View.INVISIBLE);



                            layoutFake.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutFake.setVisibility(View.INVISIBLE);
                                    layoutRequestFuel.setVisibility(View.INVISIBLE);
                                    layoutRequestFuelUserDetail.setVisibility(View.INVISIBLE);
                                }
                            });

                            layoutRequestFuel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });


                            checkMyPermission();
                            initMap();
                            mLocationClient = new FusedLocationProviderClient(MapActivity.this);

                            btnAutoLocate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getCurrLoc();
                                }
                            });

                        }

                    } else {
                        //Log.d(TAG, "Error");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void initMap() {
        if (isPermissionGranted) {
            if (isGPSenable()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                supportMapFragment.getMapAsync(this);
                //mapView.getMapAsync(this);
                //mapView.onCreate(savedInstanceState);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrLoc() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Location location = task.getResult();
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                gotoLocation(location.getLatitude(), location.getLongitude());
            }

        });
    }


    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS at your device")
                    .setPositiveButton("YES", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();

            return false;
        }
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MapActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
                ;
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);


        // set default map location
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(userLatitude, userLongitude), 7);

        getCurrLoc();
        // moves camera to coordinates
        mGoogleMap.moveCamera(point);

        FirebaseFirestore.getInstance().collection("waypoint").addSnapshotListener(new EventListener<QuerySnapshot>() {


            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mGoogleMap.clear();


                if (userType.equals("worker"))
                {

                    if (!value.isEmpty()) {

                        for (DocumentSnapshot ds : value) {
                            if (ds.getString("status").equals("pending")) {


                                Map<String, Object> mGoogleData = ds.getData();
                                LatLng latLng = new LatLng((Double) mGoogleData.get("Latitude"), (Double) mGoogleData.get("Longitude"));


                                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.markerequestfuel_foreground)));
                                Log.e("nak userid", mGoogleData.toString());
                                FirebaseFirestore.getInstance().collection("users").document(mGoogleData.get("userid").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                    @Override
                                    //kat sini dalam marker dia akan simpan semua field dalam map marker lepas kita put markerdata DocumentSnapsthot = baca all dri database
                                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> userData = new HashMap<>();
                                        userData = documentSnapshot.getData();

                                        userData.put("price", ds.get("price"));
                                        userData.put("typeoffuel", ds.get("typeoffuel"));
                                        userData.put("addresslatlng", ds.get("addresslatlng"));
                                        userData.put("waypointid", ds.getId());

                                        markerData.put(marker, userData);

                                        Log.e("nak tau marker data", markerData.toString());

                                        btnAcceptUserRequest.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Toast.makeText(MapActivity.this, "Client Request Accepted", Toast.LENGTH_LONG).show();
                                                layoutRequestFuelUserDetail.setVisibility(View.INVISIBLE);

                                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("waypoint").document(waypointID);

                                                docRef
                                                        .update("status", "complete")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                markerData.remove(marker);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Log.w(TAG, "Error updating document", e);
                                                            }
                                                        });


                                            }
                                        });
                                    }


                                });

                            }
                        }

                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                layoutRequestFuelUserDetail.setVisibility(View.VISIBLE);
                                layoutFake.setVisibility(View.VISIBLE);
                                txtUsername.setText(markerData.get(marker).get("FullName").toString());
                                txtAddressUser.setText(markerData.get(marker).get("addresslatlng").toString());
                                txtPriceFuelUser.setText("RM" +markerData.get(marker).get("price").toString());
                                txtTypeFuelUser.setText(markerData.get(marker).get("typeoffuel").toString());
                                waypointID = markerData.get(marker).get("waypointid").toString();

                                Log.e("Test Apa Ada", markerData.get(marker).toString());
                                //Toast.makeText(MapActivity.this, markerData.get(marker).get("Email").toString(), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                    }
                }
                else {


                    for (DocumentSnapshot ds : value) {
                        Log.e("suerid", ds.get("userid").toString());
                        if (ds.getString("status").equals("pending")) {
                            if (ds.get("userid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Log.e("useridsorang", ds.get("userid").toString());
                                Map<String, Object> mGoogleData = ds.getData();
                                Log.e("Masuk tak marker 2", ".");
                                LatLng latLng = new LatLng((Double) mGoogleData.get("Latitude"), (Double) mGoogleData.get("Longitude"));


                                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.markerequestfuel_foreground)));
                                Log.e("nak userid", mGoogleData.toString());
                                FirebaseFirestore.getInstance().collection("users").document(mGoogleData.get("userid").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                    @Override
                                    //kat sini dalam marker dia akan simpan semua field dalam map marker lepas kita put markerdata DocumentSnapsthot = baca all dri database
                                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> userData = new HashMap<>();
                                        userData = documentSnapshot.getData();

                                        userData.put("price", ds.get("price"));
                                        userData.put("typeoffuel", ds.get("typeoffuel"));
                                        userData.put("addresslatlng", ds.get("addresslatlng"));
                                        markerData.put(marker, userData);
                                        Log.e("nak tau marker data", markerData.toString());

                                    }


                                });
                            }

                            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    layoutRequestFuelUserDetail.setVisibility(View.VISIBLE);
                                    layoutFake.setVisibility(View.VISIBLE);
                                    txtUsername.setText(markerData.get(marker).get("FullName").toString());
                                    txtAddressUser.setText(markerData.get(marker).get("addresslatlng").toString());
                                    txtPriceFuelUser.setText("RM" + markerData.get(marker).get("price").toString());
                                    txtTypeFuelUser.setText(markerData.get(marker).get("typeoffuel").toString());


                                    Log.e("Test Apa Ada", markerData.get(marker).toString());
                                    //Toast.makeText(MapActivity.this, markerData.get(marker).get("Email").toString(), Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });
                        }
                    }





                }
            }
        });
    }


    public void checkUserfx() {

        FirebaseFirestore.getInstance().collection("waypoint")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Toast.makeText(MapActivity.this, "Pending", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            FirebaseFirestore.getInstance().collection("waypoint").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(@NonNull DocumentReference documentReference) {

                                }
                            });
                        }
                    }
                });


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            if (providerEnable) {
                Toast.makeText(this, "GPS is Enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS is not enable", Toast.LENGTH_SHORT).show();
            }
        }
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
                            Intent intent = new Intent(MapActivity.this, UserProfile.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MapActivity.this, WorkerProfileActivity.class);
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

    public void fxCheckUserType()
    {

    }
}