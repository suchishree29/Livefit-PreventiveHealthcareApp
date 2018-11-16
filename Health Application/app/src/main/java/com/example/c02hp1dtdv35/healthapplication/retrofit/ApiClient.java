package com.example.c02hp1dtdv35.healthapplication.retrofit;

import com.example.c02hp1dtdv35.healthapplication.Login.CustomLog;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.c02hp1dtdv35.healthapplication.Login.Constants.BASE_URL;



public class ApiClient {
    public static final String Google_BASE_URL = "http://maps.googleapis.com/maps/api/";

    public static final String Google_BASE_AutoComplete_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/";


    public static final String BASE_URL = "http://ec2-13-57-182-205.us-west-1.compute.amazonaws.com:3000/api/";
    private static Retrofit retrofit = null;

    private static Retrofit retrofitgoogle = null;
    private static Retrofit retrofitgoogleAutocomplete = null;





    public static Retrofit getClient()
    {
        if (retrofit==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

          /*  httpClient.setReadTimeout(5, TimeUnit.SECONDS);
            httpClient.setConnectTimeout(5, TimeUnit.SECONDS);*/
// add your other interceptors …

// add logging as last interceptor

            httpClient.connectTimeout(6000, TimeUnit.SECONDS)
                    .readTimeout(6000, TimeUnit.SECONDS)
                    .writeTimeout(6000, TimeUnit.SECONDS);

            httpClient.addInterceptor(logging);  // <-- this is the important line!


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers());
            //YCustomLog.d(String.format("Sending request %s on %s%n%s",
            //        request.url(), chain.connection(), request.headers()));
            if(request.method().compareToIgnoreCase("post")==0){
                requestLog ="\n"+requestLog+"\n"+bodyToString(request);
            }
            CustomLog.d("TAG","request"+"\n"+requestLog);

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            String responseLog = String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers());

            String bodyString = response.body().string();

            CustomLog.d("TAG","response"+"\n"+responseLog+"\n"+bodyString);

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
            //return response;
        }
    }

    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static Retrofit get_retrofit_client_google()
    {
        if (retrofitgoogle==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

          /*  httpClient.setReadTimeout(5, TimeUnit.SECONDS);
            httpClient.setConnectTimeout(5, TimeUnit.SECONDS);*/
// add your other interceptors …

// add logging as last interceptor

            httpClient.connectTimeout(6000, TimeUnit.SECONDS)
                    .readTimeout(6000, TimeUnit.SECONDS)
                    .writeTimeout(6000, TimeUnit.SECONDS);

            httpClient.addInterceptor(logging);  // <-- this is the important line!

            retrofitgoogle = new Retrofit.Builder()
                    .baseUrl(Google_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofitgoogle;
    }


    public static Retrofit get_retrofit_client_google_autocomplete()
    {
        if (retrofitgoogleAutocomplete==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

          /*  httpClient.setReadTimeout(5, TimeUnit.SECONDS);
            httpClient.setConnectTimeout(5, TimeUnit.SECONDS);*/
// add your other interceptors …

// add logging as last interceptor

            httpClient.connectTimeout(6000, TimeUnit.SECONDS)
                    .readTimeout(6000, TimeUnit.SECONDS)
                    .writeTimeout(6000, TimeUnit.SECONDS);

            httpClient.addInterceptor(logging);  // <-- this is the important line!


            retrofitgoogleAutocomplete = new Retrofit.Builder()
                    .baseUrl(Google_BASE_AutoComplete_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofitgoogleAutocomplete;
    }
}
