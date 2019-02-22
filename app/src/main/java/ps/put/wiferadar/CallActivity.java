package ps.put.wiferadar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import ps.put.wiferadar.entity.Location;
import ps.put.wiferadar.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.TimeUnit;

public class CallActivity extends AppCompatActivity implements OnMapReadyCallback, Callback<Location> {

    private static final String LOG_TAG = "CallActivity";

    private Disposable updatesDisposable = Disposables.empty();
    private Disposable timer = Disposables.empty();
    TextView textStatus;
    TextView textDuration;
    ImageView buttonHangup;
    ImageView buttonAnswer;
    TextView textDisplayName;
    RestClient restClient;
    MapView mapView;
    GoogleMap googleMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        hideBottomNavigationBar();
        textStatus = findViewById(R.id.textStatus);
        textDuration = findViewById(R.id.textDuration);
        buttonHangup = findViewById(R.id.buttonHangup);
        buttonAnswer = findViewById(R.id.buttonAnswer);
        textDisplayName = findViewById(R.id.textDisplayName);
        buttonAnswer.setOnClickListener(click -> CallManager.acceptCall());
        buttonHangup.setOnClickListener(click -> CallManager.cancelCall());


        restClient = new RestClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerReceiver(broadcastReceiver, new IntentFilter("LOKACJA"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatesDisposable = CallManager.updates()
                .doOnEach(one -> Log.i(LOG_TAG, "updatedCall " + one.toString()))
                .doOnError(throwable -> Log.e(LOG_TAG, "Error processing call", throwable))
                .subscribe(this::updateView);
        registerReceiver(broadcastReceiver, new IntentFilter("LOKACJA"));
    }

    private void updateView(GsmCall gsmCall) {
        if (gsmCall.getStatus() == GsmCall.Status.ACTIVE) {
            textStatus.setVisibility(View.GONE);
            textDuration.setVisibility(View.VISIBLE);
            startTimer();

        } else {
            textStatus.setVisibility(View.VISIBLE);
            textDuration.setVisibility(View.GONE);
        }

        if (gsmCall.getStatus() == GsmCall.Status.DISCONNECTED) {
            buttonHangup.setVisibility(View.GONE);
            buttonHangup.postDelayed(this::finish, 2000);
            stopTimer();
        } else {
            buttonHangup.setVisibility(View.VISIBLE);
        }

        String displayName = gsmCall.getDisplayName();

        textStatus.setText(gsmCall.getStatus().toString());
        textDisplayName.setText(displayName);

        if (gsmCall.getStatus() == GsmCall.Status.RINGING) {
            buttonAnswer.setVisibility(View.VISIBLE);
        } else {
            buttonAnswer.setVisibility(View.GONE);
        }
        restClient.getLocation(gsmCall.getDisplayName(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatesDisposable.dispose();
        unregisterReceiver(broadcastReceiver);
    }

    private void startTimer() {
        timer = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> textDuration.setText(toDurationString(seconds)));
    }

    private void stopTimer() {
        timer.dispose();
    }

    private void hideBottomNavigationBar() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private String toDurationString(Long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.i("MAPA ZALADOWANA", "MAPA ZALADOWANA!");
    }

    @Override
    public void onResponse(Call<Location> call, Response<Location> response) {
        Location location = response.body();
        Log.i(LOG_TAG, "Got response with location!");
        if (googleMap != null) {
            googleMap.clear();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Your caller"));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onFailure(Call<Location> call, Throwable t) {
        Log.e("FAILED REQUEST", call.request().toString());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = Double.valueOf(intent.getStringExtra("latutide"));
            double longitude = Double.valueOf(intent.getStringExtra("longitude"));
            restClient.sendLocation(new Location(latitude, longitude));
        }
    };
}
