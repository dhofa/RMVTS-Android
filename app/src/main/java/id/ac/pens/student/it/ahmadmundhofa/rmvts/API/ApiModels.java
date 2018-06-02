package id.ac.pens.student.it.ahmadmundhofa.rmvts.API;

import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("api/get-dashboard")
    Call<ResponseModel> getDashboard(@Header("Authorization") String authorization);

    @GET("api/gps")
    Call<ResponseModel> getGpsData(@Header("Authorization") String authorization);

    @GET("api/get-images")
    Call<ResponseModel> getImages(@Header("Authorization") String authorization);


//    @GET("api/districts/{path}")
//    Call<List<KecamatanResponse>> getKecamatan(@Path("path") String path);


//
//    @GET("api/order")
//    Call<List<OrderResponse>> getOrderDone(@Header("Authorization") String authorization,
//                                           @Query("status") String status);
//

//
//    @FormUrlEncoded
//    @POST("api/order/{order_id}/{status_pengiriman}")
//    Call<TakeOrderResponse> updateOrder(@Header("Authorization") String authorization,
//                                        @Path("order_id") String order_id,
//                                        @Path("status_pengiriman") String status_pengiriman,
//                                        @Field("service_id") String service_id,
//                                        @Field("origin_district_id") String origin_district_id,
//                                        @Field("destinatio_district_id") String destinatio_district_id,
//                                        @Field("weight") String weight,
//                                        @Field("p") String panjang,
//                                        @Field("l") String lebar,
//                                        @Field("t") String tinggi);
//
//
//    @FormUrlEncoded
//    @POST("api/user/set-fcm")
//    Call<SetFCMResponse> setFCM(@Header("Authorization") String authorization,
//                                @Field("fcm_id") String fcm_id);
//
//    @FormUrlEncoded
//    @POST("api/order/create")
//    Call<CreateOrderPostResponse> postKirimPaket(@Header("Authorization") String authorization,
//                                                 @Field("service_id") Integer service_id,
//                                                 @Field("origin_district_id") Integer origin_district_id,
//                                                 @Field("destinatio_district_id") Integer destinatio_district_id,
//                                                 @Field("origin_address") String origin_address,
//                                                 @Field("destinatio_address") String destinatio_address,
//                                                 @Field("consignee_name") String consignee_name,
//                                                 @Field("consignee_phone") String consignee_phone,
//                                                 @Field("item_name") String item_name,
//                                                 @Field("information") String information,
//                                                 @Field("weight") Integer weight,
//                                                 @Field("p") Integer panjang,
//                                                 @Field("l") Integer lebar,
//                                                 @Field("t") Integer tinggi
//    );
//
//    @FormUrlEncoded
//    @POST("api/register")
//    Call<RegisterResponse> goRegister(@Field("name") String name,
//                                      @Field("email") String email,
//                                      @Field("phone") String phone,
//                                      @Field("password") String password,
//                                      @Field("role") String role
//    );
//
//
//    @GET("api/service")
//    Call<ServiceResponse> getService();
//
//    @GET("api/cost")
//    Call<List<CostModel>> goCheckHarga(@Header("Authorization") String authorization,
//                                       @Query("origin_id") String origin_id,
//                                       @Query("destinatio_id") String destinatio_id,
//                                       @Query("weight") String weight,
//                                       @Query("p") String panjang,
//                                       @Query("l") String lebar,
//                                       @Query("t") String tinggi,
//                                       @Query("service_id") String service_id
//    );
}
