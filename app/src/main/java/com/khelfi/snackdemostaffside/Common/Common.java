package com.khelfi.snackdemostaffside.Common;

import com.khelfi.snackdemostaffside.Model.User;
import com.khelfi.snackdemostaffside.RemoteWebServer.APIService;
import com.khelfi.snackdemostaffside.RemoteWebServer.RetrofitClient;

/**
 * Created by norma on 23/12/2017.
 *
 */

public class Common {

    public static User currentUser;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static String codeToStatus(String code) {

        switch (code){

            case "0" :
                return "Placed";

            case "1" :
                return "On the way..";

            case "2" :
                return "Delivered";

            default:
                return "Placed";
        }

    }

    //FCM
    public static String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
