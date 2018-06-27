package id.ac.pens.student.it.ahmadmundhofa.rmvts.API;

import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiModels {

    @FormUrlEncoded
    @POST("api/users/login")
    Call<ResponseModel> goLogin(@Field("email") String email,
                                @Field("password") String password,
                                @Field("fcm_token") String fcm_token
    );

    @FormUrlEncoded
    @POST("api/users/create-user")
    Call<ResponseModel> createUser(@Field("email") String email,
                                   @Field("password") String password,
                                   @Field("owner") String owner,
                                   @Field("plate_number") String plate_number,
                                   @Field("address") String address,
                                   @Field("vehicle_type") String vehicle_type
    );

    @Multipart
    @POST("api/update-profile")
    Call<ResponseModel> updateFotoProfile(@Header("Authorization") String authorization,
                                          @Part MultipartBody.Part file_foto);

    @GET("api/get-dashboard")
    Call<ResponseModel> getDashboard(@Header("Authorization") String authorization);

    @GET("api/gps-periode")
    Call<ResponseModel> getGpsData(@Header("Authorization") String authorization,
                                   @Query("periode") String periode);

    @GET("api/get-images")
    Call<ResponseModel> getImages(@Header("Authorization") String authorization);

    @GET("api/log-activity-periode")
    Call<ResponseModel> getLogActivity(@Header("Authorization") String authorization,
                                       @Query("periode") String periode);

    @GET("api/log-vibration-periode")
    Call<ResponseModel> getLogVibration(@Header("Authorization") String authorization,
                                       @Query("periode") String periode);

    @GET("api/log-ignition-periode")
    Call<ResponseModel> getLogIgnition(@Header("Authorization") String authorization,
                                        @Query("periode") String periode);

    @GET("api/log-buzzer-periode")
    Call<ResponseModel> getLogBuzzer(@Header("Authorization") String authorization,
                                       @Query("periode") String periode);
}
