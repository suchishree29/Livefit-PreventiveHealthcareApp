package com.example.c02hp1dtdv35.healthapplication.Home;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.c02hp1dtdv35.healthapplication.R;

/**
 * Created by C02HP1DTDV35 on 5/12/18.
 */

public class CameraFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public CameraFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_camera_screen, container, false);

    }
}
