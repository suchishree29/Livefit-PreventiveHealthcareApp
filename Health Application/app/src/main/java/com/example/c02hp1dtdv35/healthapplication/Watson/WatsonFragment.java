package com.example.c02hp1dtdv35.healthapplication.Watson;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.Analytics;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

import java.util.ArrayList;
import java.util.logging.Logger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;


/**
 * Created by C02HP1DTDV35 on 5/12/18.
 */

public class WatsonFragment extends Fragment {

    EditText watsonUserInput;

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    //private Map<String,Object> context = new HashMap<>();
    private com.ibm.watson.developer_cloud.conversation.v1.model.Context context = null;
    StreamPlayer streamPlayer;
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private SpeechToText speechService;
    private TextToSpeech textToSpeech;
    private MicrophoneInputStream capture;
    private Context mContext;
    private String workspace_id;
    private String conversation_username;
    private String conversation_password;
    private String STT_username;
    private String STT_password;
    private String TTS_username;
    private String TTS_password;
    private String analytics_APIKEY;
    private SpeakerLabelsDiarization.RecoTokens recoTokens;
    private MicrophoneHelper microphoneHelper;
    private Logger myLogger;
    View view;





    public WatsonFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_watson_screen, container, false);
        //watsonUserInput=view.findViewById(R.id.watsonUserInput);

//        mContext = getActivity().getApplicationContext();
//        conversation_username = mContext.getString(R.string.conversation_username);
//        conversation_password = mContext.getString(R.string.conversation_password);
//        workspace_id = mContext.getString(R.string.workspace_id);
//        STT_username = mContext.getString(R.string.STT_username);
//        STT_password = mContext.getString(R.string.STT_password);
//        TTS_username = mContext.getString(R.string.TTS_username);
//        TTS_password = mContext.getString(R.string.TTS_password);
//        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);



        return inflater.inflate(R.layout.watson_fragment, container, false);

    }
//    private void analytics(){
//        BMSClient.getInstance().initialize(getActivity().getApplicationContext(), BMSClient.REGION_SYDNEY);
//        //Analytics is configured to record lifecycle events.
//        Analytics.init(getActivity().getApplication(), "WatBot", analytics_APIKEY, false,false, Analytics.DeviceEvent.ALL);
//        //Analytics.send();
//        myLogger = Logger.getLogger("myLogger");
//        // Send recorded usage analytics to the Mobile Analytics Service
//        Analytics.send(new ResponseListener() {
//            @Override
//            public void onSuccess(Response response) {
//                // Handle Analytics send success here.
//            }
//
//            @Override
//            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
//                // Handle Analytics send failure here.
//            }
//        });
//
//
//        Logger.send(new ResponseListener() {
//            @Override
//            public void onSuccess(Response response) {
//                // Handle Logger send success here.
//            }
//
//            @Override
//            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
//                // Handle Logger send failure here.
//            }
//        });
//    }
//
//    }





}
