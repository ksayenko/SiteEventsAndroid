package com.honeywell.stevents;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import okhttp3.MultipartBody;
import retrofit2.http.Part;


public interface RestApiService {
    @GET("stdetweb/Data/GetServerDate")
    Call<String> getServerDateString();

    @GET("stdetweb/Data/GetDataset")
    Call<String> getDataSet(@Query("datasetName")  String datasetName);

    @GET("stdetweb/Data/GetDataset")
    Call<List<Users>> getDataSetUsers(@Query("datasetName")  String datasetName);

    @GET("stdetweb/Data/GetDataset")
    Call<List<Equip_Ident>> getDataSetEquipIdent(@Query("datasetName")  String datasetName);

    @GET("stdetweb/Data/GetDataset")
    Call<List<MaintPersIdent>> getDataSetMaintPersIdent(@Query("datasetName")  String datasetName);

    @GET("stdetweb/Data/GetDataset")
    Call<List<Site_Event_Def>> getDataSetSite_Event_Def(@Query("datasetName")  String datasetName);

    @GET("stdetweb/Data/Login")
    Call<String> GetLogin(@Query("user") String userName, @Query("password") String Password);


    @POST("stdetweb/Data/UploadFile")
    Call<String> UploadFile(
            @Query("fileName") String fileName,
            @Query("user") String user,
            @Query("password") String password,
            @Body RequestBody fileBody);
}
