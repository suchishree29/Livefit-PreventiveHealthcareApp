package com.example.c02hp1dtdv35.healthapplication.FoodDataDisplay;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.Home.WatsonScreen;
import com.example.c02hp1dtdv35.healthapplication.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C02HP1DTDV35 on 5/12/18.
 */

public class FoodDisplayFragment extends Fragment {

    ImageView imageview;
    TextView textView;
    ArrayList<CardViewer>list= new ArrayList<>();
    CardView cardView;
    public FoodDisplayFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_fragment, container, false);

        imageview=(ImageView)view.findViewById(R.id.food_card_imageView);
        textView=(TextView)view.findViewById(R.id.food_card_textView);

        addData();

        int imageResource=getResources().getIdentifier("@mipmap/apple",null,"com.example.c02hp1dtdv35.healthapplication.FoodDataDisplay");
        imageview.setImageResource(imageResource);

        textView.setText("APple");
        return inflater.inflate(R.layout.activity_food_fragment, container, false);
    }
    public void addData(){

        list.add(new CardViewer("@mipmap/apple","Apple"));
        list.add(new CardViewer("@mipmap/pizza","Pizza"));
        list.add(new CardViewer("@mipmap/Salad","Salad"));
        list.add(new CardViewer("@mipmap/pancakes","Pancakes"));

    }
}
