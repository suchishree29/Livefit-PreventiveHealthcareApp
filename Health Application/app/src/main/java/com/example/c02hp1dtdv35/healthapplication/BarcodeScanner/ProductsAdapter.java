package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ProductsAdapter extends
        RecyclerView.Adapter<ProductsAdapter.ViewHolder> {


    private List<Product> products;
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView product_name,serving_size,calories,allergens,nutrimentsHigh;
        Nutriments nutriments;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            product_name = itemView.findViewById(R.id.product_name);
            serving_size = itemView.findViewById(R.id.servingSizeTxt);
            calories = itemView.findViewById(R.id.caloriesTxt);
            allergens = itemView.findViewById(R.id.allergensTxt);
            nutrimentsHigh = itemView.findViewById(R.id.nutrimentsLevelHighTxt);

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
        // Usually involves inflating a layout from XML and returning the holder
        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View productView = inflater.inflate(R.layout.log_food_list, parent,false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(productView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(ProductsAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            Product productItem = products.get(position);

            // Set item views based on your views and data model
            TextView textProduct = viewHolder.product_name;
            textProduct.setText("Product Name: " + productItem.getProductName());

            TextView textServingSize = viewHolder.serving_size;
            textServingSize.setText("Serving Size: " + productItem.getServingSize());

            TextView textCalories = viewHolder.calories;
            textCalories.setText("Calories: " + productItem.getNutriments().getEnergyValue());

            viewHolder.allergens.setText("Allergens: " + productItem.getAllergens());

            String high = productItem.getNutrientLevels().getHigh();
            if(!high.isEmpty())
                viewHolder.nutrimentsHigh.setText("High : " + high);

        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return products.size();
        }

    // Pass in the product array into the constructor
    public ProductsAdapter(List<Product> products) {
        this.products = products;
    }
}

