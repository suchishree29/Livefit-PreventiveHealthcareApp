package com.example.c02hp1dtdv35.healthapplication.LoggedFoodDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.ProductRecycleView;
import com.example.c02hp1dtdv35.healthapplication.R;
//import com.couchbase.universitylister.model.University;


import java.util.ArrayList;
import java.util.List;


public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView product_name,serving_size,calories,allergens;

        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            serving_size = itemView.findViewById(R.id.servingSizeTxt);
            calories = itemView.findViewById(R.id.caloriesTxt);
            allergens = itemView.findViewById(R.id.allergensTxt);
        }
    }

    private List<ProductRecycleView> mUniversities = new ArrayList<>();

    private Context mContext;

    public UniversityListAdapter(Context context) {
        mContext = context;



    }

    private Context getContext() {
        return mContext;
    }

    public void addUniversities(List<ProductRecycleView> universities) {
        mUniversities.addAll(universities);
    }
    public void setUniversities(List<ProductRecycleView> universities) {
        mUniversities = universities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.log_food_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductRecycleView product = mUniversities.get(position);
        holder.product_name.setText("Product Name: " +product.getName());
        holder.serving_size.setText( "Serving Size: " + product.getServing_size());
        holder.calories.setText("Calories: " + product.getCalories());
        holder.allergens.setText("Allergens: " +product.getAllergens());

    }

    @Override
    public int getItemCount() {
        return  mUniversities.size();
    }



}
