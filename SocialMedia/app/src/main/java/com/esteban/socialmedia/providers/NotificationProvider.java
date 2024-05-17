package com.esteban.socialmedia.providers;

import com.esteban.socialmedia.models.FCMBody;
import com.esteban.socialmedia.models.FCMResponse;
import com.esteban.socialmedia.retrofit.IFCMApi;
import com.esteban.socialmedia.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";
    public NotificationProvider(){

    }
    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
