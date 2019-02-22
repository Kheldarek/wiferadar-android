package ps.put.wiferadar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ps.put.wiferadar.location.LocationProvider;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SET_DEFAULT_DIALER = 123;
    private static final int REQUEST_PERMISSIONS = 100;

    private Button saveButton;
    private EditText editText;
    boolean boolean_permission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveButton = findViewById(R.id.saveButton);
        editText = findViewById(R.id.phoneNumber);
        saveButton.setOnClickListener(click -> storePhone());
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDefaultDialer();
        requestLocationPermissions();
        startService(new Intent(this, LocationProvider.class));
        SharedPreferences telefon = this.getSharedPreferences("Telefon", Context.MODE_PRIVATE);
        if (telefon.contains("telefon")) {
            editText.setText(telefon.getString("telefon", null));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode);
        }
    }

    private void checkDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        if (!telecomManager.getDefaultDialerPackage().equals(getPackageName())) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
        }
    }

    private void checkSetDefaultDialerResult(int resultCode) {
        String message;
        if (resultCode == RESULT_OK) {
            message = "User Accepted!";
        } else if (resultCode == RESULT_CANCELED) {
            message = "User declined :(";
        } else {
            message = "WTF!?";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void storePhone() {
        SharedPreferences telefon = this.getSharedPreferences("Telefon", Context.MODE_PRIVATE);
        telefon.edit().putString("telefon", editText.getText().toString()).commit();
        Toast.makeText(this, "Phone number stored", Toast.LENGTH_SHORT).show();
    }

    private void requestLocationPermissions() {
        if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if (!(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

}
