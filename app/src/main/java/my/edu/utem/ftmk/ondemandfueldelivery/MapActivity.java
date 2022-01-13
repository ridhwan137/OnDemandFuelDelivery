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
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    FloatingActionButton btnAutoLocate;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;

    TextView txtPrice, txtRon95, txtRon97, txtDiesel;
    SeekBar sliderPrice;
    CardView cvRon95, cvRon97, cvDiesel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnAutoLocate = findViewById(R.id.btnAutoLocate);
        sliderPrice = findViewById(R.id.sliderPrice);
        txtPrice = findViewById(R.id.txtPrice);

        cvRon95 = findViewById(R.id.cVRon95);
        cvRon97 = findViewById(R.id.cVRon97);
        cvDiesel= findViewById(R.id.cVDiesel);

        txtRon95 = findViewById(R.id.txtPriceRon95);
        txtRon97 = findViewById(R.id.txtPriceRon97);
        txtDiesel = findViewById(R.id.txtDiesel);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("fuel").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty())
                {
                    for (DocumentSnapshot documentSnapshot:value)
                    {
                        if (documentSnapshot.getString("name").equals("ron95"))
                        {
                            txtRon95.setText("RM"+documentSnapshot.getString("price") +"/L");
                        }
                        else if (documentSnapshot.getString("name").equals("ron97"))
                        {
                            txtRon97.setText("RM"+documentSnapshot.getString("price") +"/L");
                        }
                        else if (documentSnapshot.getString("name").equals("diesel") )
                        {
                            txtDiesel.setText("RM"+documentSnapshot.getString("price") +"/L");
                        }
                    }
                }
            }
        });


        sliderPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int step = 5;
                i = ((int) Math.round(i/step) )* step;
                seekBar.setProgress(i);
                txtPrice.setText("RM" + String.valueOf(i) +".00");
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
                txtRon95.setTextColor(Color.WHITE);
                cvRon97.setCardBackgroundColor(Color.WHITE);
                cvDiesel.setCardBackgroundColor(Color.WHITE);
            }
        });

        cvRon97.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvRon97.setCardBackgroundColor(Color.rgb(255, 199, 0));
                cvRon95.setCardBackgroundColor(Color.WHITE);
                cvDiesel.setCardBackgroundColor(Color.WHITE);
            }
        });

        cvDiesel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvDiesel.setCardBackgroundColor(Color.rgb(255, 199, 0));
                cvRon95.setCardBackgroundColor(Color.WHITE);
                cvRon97.setCardBackgroundColor(Color.WHITE);
            }
        });

        checkMyPermission();
        initMap();
        mLocationClient = new FusedLocationProviderClient(this);

        btnAutoLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrLoc();
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("first", "Ada");
                user.put("last", "Lovelace");
                user.put("born", 1815);

                // Add a new document with a generated ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(4.210484, 101.975769), 7);

        // moves camera to coordinates
        mGoogleMap.moveCamera(point);

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

}