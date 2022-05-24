package my.edu.utem.ftmk.ondemandfueldelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class internetLostConnection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_lost_connection);

        Bundle bundle = this.getIntent().getExtras();
        HashMap<String, Class> data = new HashMap<>();

        if(bundle != null) {
            data = (HashMap<String, Class>) bundle.getSerializable("HashMap");


        }

        Class context = data.get("Pages");


        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network available
                Log.e("internet", "ada");
                startActivity(new Intent(internetLostConnection.this, context));
            }

            @Override
            public void onLost(Network network) {
                // network unavailable
                Log.e("internet", "tak ada");
                //intent.putExtras(Bundle)
            }

        };

        ConnectivityManager connectivityManager =
                (ConnectivityManager) internetLostConnection.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }
}