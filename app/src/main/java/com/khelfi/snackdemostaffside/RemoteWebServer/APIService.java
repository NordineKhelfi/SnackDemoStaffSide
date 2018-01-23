package com.khelfi.snackdemostaffside.RemoteWebServer;

import com.khelfi.snackdemostaffside.Model.Message;
import com.khelfi.snackdemostaffside.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *We use Retrofit library to make HTTP requests to our remote FCM remote webserver,
 * asking it to deliver our notification ...
 *
 * Created by norma on 23/01/2018.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAG8sXlG0:APA91bHeaR0ixoyiEup3LwyjzmUTd_6KuXmN72JvkkBRNWOOVbwerQjAoE4huo9RHngKM0u3gJSw5zVVAuRAIC29ykYyWZ5-si6JX73QHfeDttkqaNrqI0IHFbnB_tTt0pQgBQgI5NH6"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Message body);

}
