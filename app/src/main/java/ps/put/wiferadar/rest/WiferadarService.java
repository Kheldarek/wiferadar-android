package ps.put.wiferadar.rest;

import ps.put.wiferadar.entity.Location;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WiferadarService {
    @POST("/location")
    Call<Boolean> sendLocation(@Query("phoneNumber") String phone, @Body Location location);

    @GET("/location")
    Call<Location> getLocation(@Query("phoneNumber") String phone);
}
