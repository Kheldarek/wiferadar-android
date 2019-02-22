package ps.put.wiferadar.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ps.put.wiferadar.CallActivity;
import ps.put.wiferadar.entity.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestClient implements Callback<Boolean> {

    WiferadarService wiferadarService;
    Retrofit retrofit;
    String myPhone;
    CallActivity context;

    public RestClient(CallActivity context) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://wiferadar-mobilki.herokuapp.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.context = context;
        wiferadarService = retrofit.create(WiferadarService.class);
        SharedPreferences telefon = context.getSharedPreferences("Telefon", Context.MODE_PRIVATE);
        if (telefon.contains("telefon")) {
            myPhone = (telefon.getString("telefon", null)).replaceAll("\\+48", "");
            Log.i("TELEFON", myPhone);
        }

    }

    public void sendLocation(Location location) {
        Log.i("Rest Client", "Sending request with location for: " + location.toString());
        wiferadarService.sendLocation(myPhone, location)
                .enqueue(this);
    }

    public void getLocation(String phoneNumber, CallActivity callActivity) {
        phoneNumber = phoneNumber.replaceAll("\\+48", "");
        Log.i("Rest Client", "Sending request to get location for: " + phoneNumber);
        wiferadarService.getLocation(phoneNumber).enqueue(callActivity);
    }

    @Override
    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

    }

    @Override
    public void onFailure(Call<Boolean> call, Throwable t) {

    }
}
