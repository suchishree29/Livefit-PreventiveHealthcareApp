package com.example.c02hp1dtdv35.healthapplication.LoggedFoodDisplay;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Nutriments;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;

import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.ShowNutriments;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;



import java.util.ArrayList;
import java.util.List;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private static List<Product> products = new ArrayList<>();
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView product_name,serving_size,calories,allergens;
        public Context context;
        Nutriments nutriments;


        public ViewHolder(View itemView, final Context context) {
            super(itemView);

            this.context = context;
            product_name = itemView.findViewById(R.id.product_name);
            serving_size = itemView.findViewById(R.id.servingSizeTxt);
            calories = itemView.findViewById(R.id.caloriesTxt);
            allergens = itemView.findViewById(R.id.allergensTxt);

            product_name.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        Product clickedProduct = products.get(pos);
                        nutriments = clickedProduct.getNutriments();
                        Intent nutrimentsIntent = new Intent(v.getContext(),ShowNutriments.class);
                        nutrimentsIntent.putExtra("nutriments", new Gson().toJson(nutriments));
                        v.getContext().startActivity(nutrimentsIntent);
                        //Toast.makeText(v.getContext(),"You clicked " + clickedProduct.getProductName(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }




    private Context mContext;

    public ProductListAdapter(Context context) {
        mContext = context;



    }

    private Context getContext() {
        return mContext;
    }


    public void setProductList(List<Product> products) {
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.log_food_list,parent,false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.product_name.setText("Product Name: " +product.getProductName());
        holder.serving_size.setText( "Serving Size: " + product.getServingSize());
        holder.calories.setText("Calories: " + product.getNutriments().getEnergy());
        holder.allergens.setText("Allergens: " +product.getAllergens());

    }

    @Override
    public int getItemCount() {
        return  products.size();
    }



}