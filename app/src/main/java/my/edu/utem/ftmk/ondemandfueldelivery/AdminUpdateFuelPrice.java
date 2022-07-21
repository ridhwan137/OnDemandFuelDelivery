package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.HashMap;
import java.util.Map;


public class AdminUpdateFuelPrice extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MaterialIconView mvCancel;
    LinearLayout lytRon95, lytRon97, lytDiesel;
    TextView txtRon95, txtRon97, txtDiesel;
    EditText etFuelNewPrice;
    Button btnUpdateNewPrice;
    String typeFuel;

    Map<String, Object> fuel = new HashMap<>();
    String typeOfFuel, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_fuel_price);

        lytRon95 = findViewById(R.id.lytRon95);
        lytRon97 = findViewById(R.id.lytRon97);
        lytDiesel = findViewById(R.id.lytDiesel);

        txtRon95 = findViewById(R.id.txtRon95);
        txtRon97 = findViewById(R.id.txtRon97);
        txtDiesel = findViewById(R.id.txtDiesel);

        etFuelNewPrice = findViewById(R.id.etFuelNewPrice);

        btnUpdateNewPrice = findViewById(R.id.btnUpdateNewPrice);

        mvCancel = findViewById(R.id.mvCancel);

        mvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getFuelPrice();

        lytRon95.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytRon95.setBackgroundResource(R.drawable.layoutcorner3);
                lytRon97.setBackgroundResource(R.drawable.layoutcorner4);
                lytDiesel.setBackgroundResource(R.drawable.layoutcorner4);
                typeFuel = "ron95";



            }
        });

        lytRon97.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytRon97.setBackgroundResource(R.drawable.layoutcorner3);
                lytRon95.setBackgroundResource(R.drawable.layoutcorner4);
                lytDiesel.setBackgroundResource(R.drawable.layoutcorner4);
                typeFuel = "ron97";

            }
        });

        lytDiesel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytDiesel.setBackgroundResource(R.drawable.layoutcorner3);
                lytRon97.setBackgroundResource(R.drawable.layoutcorner4);
                lytRon95.setBackgroundResource(R.drawable.layoutcorner4);
                typeFuel = "diesel";

            }
        });



        btnUpdateNewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("fuel").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {

                            for (DocumentSnapshot documentSnapshot : value) {
                                try {
                                    if (typeFuel.equals("ron95") && documentSnapshot.getString("name").equals("ron95")) {
                                        if (!(etFuelNewPrice.getText().toString().isEmpty()))
                                        {
                                            fuel.put("price", etFuelNewPrice.getText().toString());
                                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("fuel").document("xYAzW8r2d7fQ9bRacLrb");
                                            documentReference.update(fuel);

                                            Toast.makeText(getApplicationContext(), "Succesfully update new price for Ron95", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Failed. Can't enter empty value", Toast.LENGTH_SHORT).show();

                                        }

                                    } else if (typeFuel.equals("ron97")  && documentSnapshot.getString("name").equals("ron97")) {
                                        if (!(etFuelNewPrice.getText().toString().isEmpty()))
                                        {
                                            fuel.put("price", etFuelNewPrice.getText().toString());
                                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("fuel").document("Wff6wZx6Ivai7aa4qrrk");
                                            documentReference.update(fuel);

                                            Toast.makeText(getApplicationContext(), "Succesfully update new price for Ron97", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Failed. Can't enter empty value", Toast.LENGTH_SHORT).show();

                                        }



                                    } else if (typeFuel.equals("diesel") && documentSnapshot.getString("name").equals("diesel")) {

                                        if (!(etFuelNewPrice.getText().toString().isEmpty()))
                                        {
                                            fuel.put("price", etFuelNewPrice.getText().toString());
                                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("fuel").document("Ve1LdysHWCV4yVvBwPzn");
                                            documentReference.update(fuel);

                                            Toast.makeText(getApplicationContext(), "Succesfully update new price for Diesel", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {

                                            Toast.makeText(getApplicationContext(), "Failed. Can't enter empty value", Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), "Please select the fuel", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }


                    }

                });

            }

        });










    }

    public void getFuelPrice(){
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

    }
}