package com.esteban.socialmedia.retrofit;

import com.esteban.socialmedia.models.FCMBody;
import com.esteban.socialmedia.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAp7KCZ7I:APA91bGDsTARig7Lfw-98OIv2uU2J6LxhcctL3RKr6lUM-h6cC8-8mhzRaoWLDAet0j4DdcRI8XE5ZVI-RmcrQN4WkwNqIsW-Bzai5BN6HrGgcuvqd5YFtX3LNg5wNeCYv6Y2FKo3Un-"
    })
    @POST("fcm/send")
    Call<FCMResponse>send(@Body FCMBody body);
}
