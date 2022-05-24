package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.util.Random;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Map Activity";
    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    FloatingActionButton btnAutoLocate;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;

    double userLatitude, userLongitude;
    String typeOfFuel;
    String petrolStation;
    Double prices = 10.00, fuelLitre = 0.0;
    double totalPrice;

    Marker workerMarker;

    Map<Marker, Map<String, Object>> markerData = new HashMap<>();

    String userType;
    String waypointID;
    String addresswaypoint;
    String url;


    TextView txtPrice, txtRon95, txtRon97, txtDiesel, txtUsername, txtAddressUser, txtTypeFuelUser,
            txtPriceFuelUser, txtMobileNumOrderDetails, txtAddressOrderDetails, txtStationOrderDetails,
            txtFuelPetrolUser, txtMobileNumberUser, txtTotalPriceMarkerOrderDetails,
            txtPetrolOrderDetails, txtPetrolCapacityOrderDetails, txtPriceOrderDetails, txtCustomerNameOrderDetails,
            txtTotalPriceOrderDetails, txtEditOrderDetails;

    EditText etEnterPreferredAddress;
    SeekBar sliderPrice;
    CardView cvRon95, cvRon97, cvDiesel, cVCurrentLoc, cVPreferredAddress, cVPetronas, cVPetron, cVShell;
    Button btnRequest, btnRequestMenu, btnAcceptUserRequest, btnNextChooseLocMenu, btnBackChoosePetrolMenu,
            btnNextChoosePetrolMenu, btnPlaceOrder;
    ImageView profileNavigation, deliveryNavigation, ivProfilePic, ivProfilePicMarker;

    LinearLayout layoutHomeNavigation, layoutRequestFuel, layoutFake, layoutRequestFuelUserDetail, chooseLocationMenu, choosePetrolMenu, btnRequestFuelMenu, layoutOrderDetails;
    List<Address> addresslatlng;
    Geocoder geocoder;
    LocationManager locationManager;



    double Ron95price;

    Map<String, Object> user = new HashMap<>();
    Map<String, Object> waypoint = new HashMap<>();
    Map<String, Object> userData = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Date date = new Date();
    Date dateAccept = new Date();
    Random rand = new Random();
    int upperbound = 999999;
    int orderRand = rand.nextInt(upperbound);
    String orderNum = "OR" + String.valueOf(orderRand);
    double workerlatitude = 0, workerlongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network available
                Log.e("internet", "ada");

            }

            @Override
            public void onLost(Network network) {
                // network unavailable
                Log.e("internet", "tak ada");
                Intent intent = new Intent(MapActivity.this, internetLostConnection.class);
                Bundle bundle = new Bundle();
                HashMap<String, Class> data = new HashMap<>();
                data.put("Pages", MapActivity.class);
                bundle.putSerializable("HashMap", data);
                intent.putExtras(bundle);
                startActivity(intent);
                //intent.putExtras(Bundle)
            }

        };


        ConnectivityManager connectivityManager =
                (ConnectivityManager) MapActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }






        geocoder = new Geocoder(this, Locale.getDefault());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        int height = displayMetrics.heightPixels;

        btnAutoLocate = findViewById(R.id.btnAutoLocate);
        btnRequest = findViewById(R.id.btnRequest);
        btnRequestMenu = findViewById(R.id.btnRequestMenu);
        btnAcceptUserRequest = findViewById(R.id.btnAcceptUserRequest);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnRequestFuelMenu = findViewById(R.id.btnRequestFuelMenu);
        btnNextChooseLocMenu = findViewById(R.id.btnNextChooseLocMenu);

        btnBackChoosePetrolMenu = findViewById(R.id.btnBackChoosePetrolMenu);
        btnNextChoosePetrolMenu = findViewById(R.id.btnNextChoosePetrolMenu);


        //Navigation


        sliderPrice = findViewById(R.id.sliderPrice);
        txtPrice = findViewById(R.id.txtPrice);


        etEnterPreferredAddress = findViewById(R.id.etEnterPreferredAddress);

        //declare all cardview
        cVCurrentLoc = findViewById(R.id.cVCurrentLoc);
        cVPreferredAddress = findViewById(R.id.cVPreferredAddress);

        cVPetronas = findViewById(R.id.cVPetronas);
        cVPetron = findViewById(R.id.cVPetron);
        cVShell = findViewById(R.id.cVShell);

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
        txtFuelPetrolUser = findViewById(R.id.txtFuelPetrolUser);
        txtMobileNumberUser = findViewById(R.id.txtMobileNumberUser);
        txtTotalPriceMarkerOrderDetails = findViewById(R.id.txtTotalPriceMarkerOrderDetails);

        //all textview in customer order details layout
        txtCustomerNameOrderDetails = findViewById(R.id.txtCustomerNameOrderDetails);
        txtMobileNumOrderDetails = findViewById(R.id.txtMobileNumOrderDetails);
        txtAddressOrderDetails = findViewById(R.id.txtAddressOrderDetails);
        txtStationOrderDetails = findViewById(R.id.txtStationOrderDetails);
        txtPetrolOrderDetails = findViewById(R.id.txtPetrolOrderDetails);
        txtPetrolCapacityOrderDetails = findViewById(R.id.txtPetrolCapacityOrderDetails);
        txtPriceOrderDetails = findViewById(R.id.txtPriceOrderDetails);
        txtTotalPriceOrderDetails = findViewById(R.id.txtTotalPriceOrderDetails);
        txtEditOrderDetails = findViewById(R.id.txtEditOrderDetails);


        chooseLocationMenu = findViewById(R.id.chooseLocationMenu);
        choosePetrolMenu = findViewById(R.id.choosePetrolMenu);

        layoutRequestFuel = findViewById(R.id.layoutRequestFuel);
        layoutRequestFuelUserDetail = findViewById(R.id.layoutRequestFuelUserDetail);
        layoutOrderDetails = findViewById(R.id.layoutOrderDetails);
        layoutFake = findViewById(R.id.layoutFake);
        layoutHomeNavigation = findViewById(R.id.layoutHomeNavigation);

        //declare imageview
        ivProfilePic = findViewById(R.id.ivProfilePic);
        ivProfilePicMarker = findViewById(R.id.ivProfilePicMarker);

        // fxCheckUserType();
        //Log.e("test login user type", userType);
        // if (userType.equals("client"))
        //{

        //initialize and assign variable bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //set Map Selected
        bottomNavigationView.setSelectedItemId(R.id.mapNavigation);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.deliveryNavigation:
                        startActivity(new Intent(getApplicationContext(), deliveryStatusCustomerSide.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:

                        return true;

                    case R.id.profileNavigation:
                        startActivity(new Intent(getApplicationContext(), customerProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

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

                            btnAcceptUserRequest.setVisibility(View.INVISIBLE);

                            btnRequestFuelMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutFake.setVisibility(View.VISIBLE);
                                    chooseLocationMenu.setVisibility(View.VISIBLE);
                                }
                            });

                            cVCurrentLoc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cVCurrentLoc.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cVPreferredAddress.setCardBackgroundColor(Color.WHITE);
                                    etEnterPreferredAddress.getBackground().setColorFilter(Color.argb(255, 234, 233, 233), PorterDuff.Mode.SRC_ATOP);
                                    etEnterPreferredAddress.setEnabled(false);

                                    getCurrLoc();


                                }
                            });

                            cVPreferredAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cVPreferredAddress.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cVCurrentLoc.setCardBackgroundColor(Color.WHITE);
                                    etEnterPreferredAddress.getBackground().setColorFilter(Color.rgb(226, 177, 2), PorterDuff.Mode.SRC_ATOP);

                                    etEnterPreferredAddress.setEnabled(true);


                                   /* if (cVPreferredAddress.isClickable())
                                    {
                                        String preferredAddress = etEnterPreferredAddress.getText().toString();
                                        Log.e("Nohhha alamat", etEnterPreferredAddress.getText().toString() );

                                        LatLng latLng = getLongLatFromAddress(preferredAddress);
                                        Log.e("alamat", latLng.toString());

                                        userLatitude = latLng.latitude;
                                        userLongitude = latLng.longitude;
                                    }*/

                                   /* if (etEnterPreferredAddress.getText().toString().isEmpty())
                                    {
                                        Log.e(etEnterPreferredAddress.toString(), "kosong: ");
                                    }
                                    else
                                    {
                                        String preferredAddress = etEnterPreferredAddress.getText().toString();
                                        Log.e("Nohhha alamat", etEnterPreferredAddress.getText().toString() );

                                        LatLng latLng = getLongLatFromAddress(preferredAddress);
                                        Log.e("alamat", latLng.toString());

                                        userLatitude = latLng.latitude;
                                        userLongitude = latLng.longitude;

                                        //addresslatlng = etEnterPreferredAddress.getText().toString()
                                    }*/

                                }
                            });


                            btnNextChooseLocMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    choosePetrolMenu.setVisibility(View.VISIBLE);
                                    chooseLocationMenu.setVisibility(View.INVISIBLE);
                                }
                            });

                            cVPetronas.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cVPetronas.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cVPetron.setCardBackgroundColor(Color.WHITE);
                                    cVShell.setCardBackgroundColor(Color.WHITE);
                                    petrolStation = "Petronas";
                                }
                            });

                            cVPetron.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cVPetron.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cVPetronas.setCardBackgroundColor(Color.WHITE);
                                    cVShell.setCardBackgroundColor(Color.WHITE);
                                    petrolStation = "Petron";
                                }
                            });

                            cVShell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cVShell.setCardBackgroundColor(Color.rgb(255, 199, 0));
                                    cVPetronas.setCardBackgroundColor(Color.WHITE);
                                    cVPetron.setCardBackgroundColor(Color.WHITE);
                                    petrolStation = "Shell";
                                }
                            });

                            btnBackChoosePetrolMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    chooseLocationMenu.setVisibility(View.VISIBLE);
                                    choosePetrolMenu.setVisibility(View.INVISIBLE);
                                }
                            });

                            btnNextChoosePetrolMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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

                                                double Ron95price = Double.parseDouble(documentSnapshot.getString("price"));
                                                Log.e("tngok minyak 95", String.valueOf(Ron95price));
                                                txtRon95.setText("RM" + String.format("%.2f", Ron95price) + "/L");

                                            } else if (documentSnapshot.getString("name").equals("ron97")) {

                                                double Ron97price = Double.parseDouble(documentSnapshot.getString("price"));
                                                txtRon97.setText("RM" + String.format("%.2f", Ron97price) + "/L");


                                            } else if (documentSnapshot.getString("name").equals("diesel")) {

                                                double Dieselprice = Double.parseDouble(documentSnapshot.getString("price"));
                                                txtDiesel.setText("RM" + String.format("%.2f", Dieselprice) + "/L");


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

                            txtEditOrderDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    layoutRequestFuel.setVisibility(View.VISIBLE);
                                    layoutOrderDetails.setVisibility(View.INVISIBLE);
                                    layoutFake.setVisibility(View.VISIBLE);
                                    btnAutoLocate.setVisibility(View.VISIBLE);
                                    btnRequestFuelMenu.setVisibility(View.VISIBLE);
                                    layoutHomeNavigation.setVisibility(View.VISIBLE);
                                }
                            });

                            btnRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    layoutFake.setVisibility(View.VISIBLE);
                                    btnAutoLocate.setVisibility(View.INVISIBLE);
                                    btnRequestFuelMenu.setVisibility(View.INVISIBLE);
                                    layoutHomeNavigation.setVisibility(View.INVISIBLE);
                                    bottomNavigationView.setVisibility(View.INVISIBLE);
                                    /*layoutOrderDetails.setY(height);
                                    layoutOrderDetails.animate().translationY(0).setDuration(500).setStartDelay(0);*/
                                    layoutOrderDetails.setVisibility(View.VISIBLE);
                                    displayProfile();

                                    if (cVPreferredAddress.isClickable()) {
                                        if (etEnterPreferredAddress.getText().toString().isEmpty()) {
                                            Log.e("Koosong", etEnterPreferredAddress.getText().toString());

                                            try {
                                                addresslatlng = geocoder.getFromLocation(userLatitude, userLongitude, 1);
                                            } catch (IOException exception) {
                                                exception.printStackTrace();
                                            }

                                            addresswaypoint = addresslatlng.get(0).getAddressLine(0);
                                            txtAddressOrderDetails.setText(addresswaypoint.toString());
                                        } else {

                                            String preferredAddress = etEnterPreferredAddress.getText().toString();
                                            Log.e("Nohhha alamat", etEnterPreferredAddress.getText().toString());

                                            LatLng latLng = getLongLatFromAddress(preferredAddress);
                                            Log.e("alamat", latLng.toString());

                                            userLatitude = latLng.latitude;
                                            userLongitude = latLng.longitude;

                                            addresswaypoint = etEnterPreferredAddress.getText().toString();
                                            txtAddressOrderDetails.setText(addresswaypoint.toString());
                                        }
                                    } else {
                                        Log.e(etEnterPreferredAddress.toString(), "kosong: ");
                                        getCurrLoc();


                                    }

                                    fxGetFuelPriceNew();


                                    txtStationOrderDetails.setText(petrolStation);
                                    txtPetrolOrderDetails.setText(typeOfFuel);
                                    txtPriceOrderDetails.setText("RM" + String.format("%.2f", prices));
                                    totalPrice = prices + 5;
                                    txtTotalPriceOrderDetails.setText("RM" + String.format("%.2f", totalPrice));


                                    DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();

                                                if (document.exists()) {
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                                    txtCustomerNameOrderDetails.setText(document.getData().get("FullName").toString());
                                                    txtMobileNumOrderDetails.setText(document.getData().get("PhoneNum").toString());

                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });


                                }
                            });

                            btnPlaceOrder.setOnClickListener(new View.OnClickListener() {


                                @Override
                                public void onClick(View view) {


                                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                                            //user = documentSnapshot.getData();

                                            if (cVPreferredAddress.isClickable()) {
                                                if (etEnterPreferredAddress.getText().toString().isEmpty()) {
                                                    Log.e("Koosong", etEnterPreferredAddress.getText().toString());

                                                    try {
                                                        addresslatlng = geocoder.getFromLocation(userLatitude, userLongitude, 1);
                                                    } catch (IOException exception) {
                                                        exception.printStackTrace();
                                                    }

                                                    addresswaypoint = addresslatlng.get(0).getAddressLine(0);
                                                } else {

                                                    String preferredAddress = etEnterPreferredAddress.getText().toString();
                                                    Log.e("Nohhha alamat", etEnterPreferredAddress.getText().toString());

                                                    LatLng latLng = getLongLatFromAddress(preferredAddress);
                                                    Log.e("alamat", latLng.toString());

                                                    userLatitude = latLng.latitude;
                                                    userLongitude = latLng.longitude;

                                                    addresswaypoint = etEnterPreferredAddress.getText().toString();
                                                }
                                            } else {
                                                Log.e(etEnterPreferredAddress.toString(), "kosong: ");
                                                getCurrLoc();


                                            }


                                            user.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            user.put("priceunit", String.format("%.2f", prices));
                                            user.put("price", String.format("%.2f", totalPrice));
                                            user.put("Latitude", userLatitude);
                                            user.put("Longitude", userLongitude);
                                            user.put("petrolstation", petrolStation);

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
                                                            if (queryDocumentSnapshots.size() == 0) {
                                                                FirebaseFirestore.getInstance().collection("waypoint").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull DocumentReference documentReference) {
                                                                        Toast.makeText(MapActivity.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                                                                        Log.e("Noh Test", user.toString());
                                                                        layoutRequestFuel.setVisibility(View.INVISIBLE);
                                                                        layoutOrderDetails.setVisibility(View.VISIBLE);
                                                                        chooseLocationMenu.setVisibility(View.INVISIBLE);
                                                                        choosePetrolMenu.setVisibility(View.INVISIBLE);
                                                                        layoutFake.setVisibility(View.INVISIBLE);
                                                                        layoutOrderDetails.setVisibility(View.INVISIBLE);
                                                                        btnAutoLocate.setVisibility(View.VISIBLE);
                                                                        btnRequestFuelMenu.setVisibility(View.VISIBLE);
                                                                        layoutHomeNavigation.setVisibility(View.VISIBLE);

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
                                    chooseLocationMenu.setVisibility(View.INVISIBLE);
                                    choosePetrolMenu.setVisibility(View.INVISIBLE);
                                    layoutFake.setVisibility(View.INVISIBLE);
                                    layoutRequestFuel.setVisibility(View.INVISIBLE);
                                    layoutRequestFuelUserDetail.setVisibility(View.INVISIBLE);
                                    btnAutoLocate.setVisibility(View.VISIBLE);
                                    btnRequestFuelMenu.setVisibility(View.VISIBLE);
                                    // layoutOrderDetails.animate().translationY(height).setDuration(1000).setStartDelay(0);
                                    layoutOrderDetails.setVisibility(View.INVISIBLE);
                                    layoutHomeNavigation.setVisibility(View.VISIBLE);
                                    bottomNavigationView.setVisibility(View.VISIBLE);

                                }
                            });

                            layoutOrderDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });

                            chooseLocationMenu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

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
                                    layoutOrderDetails.setVisibility(View.INVISIBLE);
                                }
                            });


                        } else {
                            btnRequestMenu.setVisibility(View.INVISIBLE);
                            btnRequestFuelMenu.setVisibility(View.INVISIBLE);

                            //perform itemselectedlistener
                            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                    switch (menuItem.getItemId())
                                    {
                                        case R.id.deliveryNavigation:
                                            startActivity(new Intent(getApplicationContext(), deliveryStatusWorkerSide.class));
                                            overridePendingTransition(0,0);
                                            return true;

                                        case R.id.mapNavigation:

                                            return true;

                                        case R.id.profileNavigation:
                                            startActivity(new Intent(getApplicationContext(), customerProfile.class));
                                            overridePendingTransition(0,0);
                                            return true;
                                    }
                                    return false;
                                }
                            });


                            FirebaseFirestore.getInstance().collection("order")
                                    .whereEqualTo("orderStatus", "active")
                                    .whereEqualTo("workername", document.getData().get("FullName").toString())
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.e("dekat worker", String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                                    LocationListener locationListenerGPS = new LocationListener() {

                                        @Override
                                        public void onLocationChanged(android.location.Location location) {
                                            double latitude = location.getLatitude();
                                            double longitude = location.getLongitude();
                                            workerlatitude = latitude;
                                            workerlongitude = longitude;
                                            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                                            //Toast.makeText(MapActivity.this, msg, Toast.LENGTH_LONG).show();
                                            Log.d(String.valueOf(latitude), String.valueOf(longitude));
                                            Map<String, Object> currentWorkerLoc = new HashMap<>();
                                            currentWorkerLoc.put("currentWorkerLatitude", latitude);
                                            currentWorkerLoc.put("currentWorkerLongitude", longitude);
                                            if (!queryDocumentSnapshots.getDocuments().isEmpty()) {


                                                FirebaseFirestore.getInstance().collection("order").document(queryDocumentSnapshots.getDocuments().get(0).get("waypointid").toString()).update(currentWorkerLoc);
                                            }
                                        }


                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                        }

                                        @Override
                                        public void onProviderEnabled(String provider) {

                                        }

                                        @Override
                                        public void onProviderDisabled(String provider) {

                                        }
                                    };

                                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

                                    isLocationEnabled();

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


                if (userType.equals("worker")) {

                    if (!value.isEmpty()) {

                        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                Location location = task.getResult();
                                userLatitude = location.getLatitude();
                                userLongitude = location.getLongitude();
                                gotoLocation(location.getLatitude(), location.getLongitude());
                            }


                            for (DocumentSnapshot ds : value) {
                                if (ds.getString("status").equals("pending")) {


                                    Map<String, Object> mGoogleData = ds.getData();
                                    LatLng latLng = new LatLng((Double) mGoogleData.get("Latitude"), (Double) mGoogleData.get("Longitude"));
                                    Log.e("latlng client", latLng.toString());

                                    Log.e(String.valueOf(userLatitude), String.valueOf(userLongitude));

                                    if (userLatitude > (Double) mGoogleData.get("Latitude") - 0.01 && userLatitude < (Double) mGoogleData.get("Latitude") + 0.01 && userLongitude > (Double) mGoogleData.get("Longitude") - 0.01 && userLongitude < (Double) mGoogleData.get("Longitude") + 0.01) {


                                        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.markerequestfuel_foreground)));
                                        Log.e("nak tngok mgoogle data ada apa mat", mGoogleData.toString());
                                        FirebaseFirestore.getInstance().collection("users").document(mGoogleData.get("userid").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                            @Override
                                            //kat sini dalam marker dia akan simpan semua field dalam map marker lepas kita put markerdata DocumentSnapsthot = baca all dri database
                                            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {

                                                userData = documentSnapshot.getData();

                                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                        if (task.isSuccessful()) {


                                                            userData.put("userid", ds.get("userid"));
                                                            userData.put("price", ds.get("price"));
                                                            userData.put("typeoffuel", ds.get("typeoffuel"));
                                                            userData.put("priceunit", ds.get("priceunit"));
                                                            userData.put("addresslatlng", ds.get("addresslatlng"));
                                                            userData.put("petrolstation", ds.get("petrolstation"));
                                                            userData.put("waypointid", ds.getId());
                                                            userData.put("orderNumber", orderNum);
                                                            userData.put("workername", task.getResult().get("FullName").toString());
                                                            userData.put("plateNumber", task.getResult().get("plateNumber").toString());
                                                            userData.put("workerid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            userData.put("date", dateAccept);
                                                            userData.put("orderStatus", "active");

                                                            markerData.put(marker, userData);

                                                            Log.e("nak tau marker data", markerData.toString());

                                                            btnAcceptUserRequest.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {


                                                                    db.collection("order").document(waypointID)
                                                                            .set(userData)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                                    LocationListener locationListenerGPS = new LocationListener() {

                                                                                        @Override
                                                                                        public void onLocationChanged(android.location.Location location) {
                                                                                            double latitude = location.getLatitude();
                                                                                            double longitude = location.getLongitude();
                                                                                            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                                                                                            Toast.makeText(MapActivity.this, msg, Toast.LENGTH_LONG).show();
                                                                                            Log.d(String.valueOf(latitude), String.valueOf(longitude));
                                                                                            Map<String, Object> currentWorkerLoc = new HashMap<>();
                                                                                            currentWorkerLoc.put("currentWorkerLatitude", latitude);
                                                                                            currentWorkerLoc.put("currentWorkerLongitude", longitude);
                                                                                            FirebaseFirestore.getInstance().collection("order").document(waypointID).update(currentWorkerLoc);
                                                                                        }


                                                                                        @Override
                                                                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onProviderEnabled(String provider) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onProviderDisabled(String provider) {

                                                                                        }
                                                                                    };

                                                                                    //locationManager.removeUpdates(locationListenerGPS);

                                                                                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                                                        // TODO: Consider calling
                                                                                        //    ActivityCompat#requestPermissions
                                                                                        // here to request the missing permissions, and then overriding
                                                                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                                                        //                                          int[] grantResults)
                                                                                        // to handle the case where the user grants the permission. See the documentation
                                                                                        // for ActivityCompat#requestPermissions for more details.
                                                                                        return;
                                                                                    }
                                                                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

                                                                                    isLocationEnabled();

                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w(TAG, "Error writing document", e);
                                                                                }
                                                                            });

                                                                    Toast.makeText(MapActivity.this, "Client Request Accepted", Toast.LENGTH_LONG).show();
                                                                    layoutRequestFuelUserDetail.setVisibility(View.INVISIBLE);

                                                                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("waypoint").document(waypointID);

                                                                    docRef
                                                                            .update("status", "ondelivery", "workerid", FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                                    }
                                                });


                                            }


                                        });

                                    }
                                }
                            }
                        });

                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                layoutRequestFuelUserDetail.setVisibility(View.VISIBLE);
                                url = markerData.get(marker).get("Picture URL").toString();
                                Picasso.with(MapActivity.this).load(url).into(ivProfilePicMarker);
                                layoutFake.setVisibility(View.VISIBLE);
                                txtUsername.setText(markerData.get(marker).get("FullName").toString());
                                txtAddressUser.setText(markerData.get(marker).get("addresslatlng").toString());
                                txtPriceFuelUser.setText("RM" + markerData.get(marker).get("priceunit").toString());
                                txtTotalPriceMarkerOrderDetails.setText("RM" + markerData.get(marker).get("price").toString());
                                txtMobileNumberUser.setText(markerData.get(marker).get("PhoneNum").toString());
                                txtFuelPetrolUser.setText(markerData.get(marker).get("petrolstation").toString());
                                txtTypeFuelUser.setText(markerData.get(marker).get("typeoffuel").toString());
                                waypointID = markerData.get(marker).get("waypointid").toString();

                                Log.e("Test Apa Ada", markerData.get(marker).toString());
                                //Toast.makeText(MapActivity.this, markerData.get(marker).get("Email").toString(), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                    }
                } else {

                    addworkermarker();


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
                                        userData.put("priceunit", ds.get("priceunit"));
                                        userData.put("petrolstation", ds.get("petrolstation"));
                                        userData.put("waypointid", ds.getId());


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
                                    url = markerData.get(marker).get("Picture URL").toString();
                                    Picasso.with(MapActivity.this).load(url).into(ivProfilePicMarker);
                                    txtUsername.setText(markerData.get(marker).get("FullName").toString());
                                    txtAddressUser.setText(markerData.get(marker).get("addresslatlng").toString());
                                    txtPriceFuelUser.setText("RM" + markerData.get(marker).get("priceunit").toString());
                                    txtTotalPriceMarkerOrderDetails.setText("RM" + markerData.get(marker).get("price").toString());
                                    txtMobileNumberUser.setText(markerData.get(marker).get("PhoneNum").toString());
                                    txtFuelPetrolUser.setText(markerData.get(marker).get("petrolstation").toString());
                                    txtTypeFuelUser.setText(markerData.get(marker).get("typeoffuel").toString());
                                    waypointID = markerData.get(marker).get("waypointid").toString();


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

    public void addworkermarker() {


        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("orderStatus", "active")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        Log.e("tulis apa2", "ada benda berubah");

                        if (mGoogleMap != null) {
                            Log.e("tulis apa2", "googlemapready");
                            if (!queryDocumentSnapshots.isEmpty()) {
                                Log.e("tulis apa2", "ada order");
                                if (queryDocumentSnapshots.getDocuments().get(0).get("currentWorkerLatitude") != null) {
                                    Log.e("tulis apa2", "worker dah gerak");
                                    LatLng latLng = new LatLng(queryDocumentSnapshots.getDocuments().get(0).getDouble("currentWorkerLatitude"), queryDocumentSnapshots.getDocuments().get(0).getDouble("currentWorkerLongitude"));

                                    if (workerMarker == null) {
                                        Log.e("tulis apa2", "kalau marker x dak lgi, dia tambah");
                                        workerMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.riderdeliverymarker_foreground)));
                                        Log.e("tulis apa2", latLng.toString());

                                    } else {
                                        Log.e("tulis apa2", "marker dh ada mat, set position");
                                        workerMarker.setPosition(latLng);
                                    }
                                }

                            }
                        }


                    }
                });


    }

    public void fxGetUserMobileNum() {
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        txtMobileNumberUser.setText(document.getData().get("PhoneNum").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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

    public void toUserProfile(View v) {

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
                    } else {
                        Log.e("tk dak pa", "tak dak");
                    }

                }
            }
        });
    }

    public LatLng getLongLatFromAddress(String address) {

        LatLng LatLng = null;
        try {
            List<Address> addressReceiver = geocoder.getFromLocationName(address, 5);

            Address location = addressReceiver.get(0);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng = new LatLng(latitude, longitude);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return LatLng;

    }

    public void fxgetFuelPrice() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("fuel").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.getString("name").equals("ron95")) {

                            Ron95price = Double.parseDouble(documentSnapshot.getString("price"));
                            txtRon95.setText("RM" + String.format("%.2f", Ron95price) + "/L");

                            if (typeOfFuel == "RON95") {
                                fuelLitre = (prices / Ron95price);
                                Log.e("tngok sldier prices", String.valueOf(prices));
                                Log.e("tngok harga minyak dari database", String.valueOf(Ron95price));
                                Log.e("tngok total litre", String.valueOf(fuelLitre));
                                txtPetrolCapacityOrderDetails.setText(String.valueOf(fuelLitre));
                            }


                        } else if (documentSnapshot.getString("name").equals("ron97")) {

                            double Ron97price = Double.parseDouble(documentSnapshot.getString("price"));
                            txtRon97.setText("RM" + String.format("%.2f", Ron97price) + "/L");

                            if (typeOfFuel == "RON97") {
                                fuelLitre = (prices / Ron97price);
                                Log.e("tngok sldier prices", String.valueOf(prices));
                                Log.e("tngok harga minyak dari database", String.valueOf(Ron97price));
                                Log.e("tngok sldier prices", String.valueOf(fuelLitre));
                                txtPetrolCapacityOrderDetails.setText(String.valueOf(String.format("%.2f", fuelLitre)));
                            }


                        } else if (documentSnapshot.getString("name").equals("diesel")) {

                            double Dieselprice = Double.parseDouble(documentSnapshot.getString("price"));
                            txtDiesel.setText("RM" + String.format("%.2f", Dieselprice) + "/L");

                            if (typeOfFuel == "DIESEL") {
                                fuelLitre = (prices / Dieselprice);
                                Log.e("tngok sldier prices", String.valueOf(prices));
                                Log.e("tngok harga minyak dari database", String.valueOf(Dieselprice));
                                Log.e("tngok sldier prices", String.valueOf(fuelLitre));
                                txtPetrolCapacityOrderDetails.setText(String.valueOf(fuelLitre));
                            }


                        }
                    }
                }

            }
        });
    }

    public void fxGetFuelPriceNew() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("fuel").whereEqualTo("name", typeOfFuel.toLowerCase()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {

                double fuelPrice = Double.parseDouble(queryDocumentSnapshots.getDocuments().get(0).getString("price"));

                fuelLitre = (prices / fuelPrice);
                Log.e("tngok sldier prices", String.valueOf(prices));
                Log.e("tngok harga minyak dari database", String.valueOf(fuelPrice));
                Log.e("tngok total litre", String.valueOf(fuelLitre));
                String fuelformat = String.format("%.2f", fuelLitre) + "/L";
                txtPetrolCapacityOrderDetails.setText(fuelformat);

            }
        });
    }

    public void displayProfile(){

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ProfileActivity", "DocumentSnapshot data: " + document.getData());



                        url = document.getData().get("Picture URL").toString();
//                        new EditProfileActivity.FetchImage(url).start();
                        Picasso.with(MapActivity.this).load(url).into(ivProfilePic);

                    } else {
                        Log.d("ProfileActivity", "No such document");
                    }
                } else {
                    Log.d("ProfileActivity", "get failed with ", task.getException());
                }
            }
        });

    }


    protected void onResume() {
        super.onResume();
        isLocationEnabled();
    }

    private void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            //alert.show();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void fxCheckUserType() {

    }
}