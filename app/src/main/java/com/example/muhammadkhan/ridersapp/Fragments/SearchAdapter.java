package com.example.muhammadkhan.ridersapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.DriverProfileViewForPassenger;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Muhammad Khan on 15/04/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>  {

    Context context;
    private List<Driver> list;
    private LayoutInflater inflater;

    public SearchAdapter(Context context){
        this.context=context;
    }
    public void addList(List<Driver> drivers){
        this.list=drivers;
    }
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.driver_search_item_view,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.MyViewHolder holder, int position) {
        Driver driver=list.get(position);
        holder.driverName.setText(driver.getDriverName());
        holder.modelName.setText(driver.getModelName());
        holder.pricePerKilometer.setText(driver.getPricePerKilometer()+" Rs.");
        holder.driverCity.setText(driver.getDriverCity());
        holder.vehicleType.setText(driver.getVehicleType());
        if(driver.getProfileImage().equals("No")){
            holder.profileImage.setImageResource(R.mipmap.profile);
        }else {
            Glide.with(context).load(Uri.parse(driver.getProfileImage()).toString()).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView profileImage;
        private TextView driverName;
        private TextView modelName;
        private TextView pricePerKilometer;
        private TextView vehicleType;
        private TextView driverCity;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            profileImage=itemView.findViewById(R.id.passenger_image);
            driverName=itemView.findViewById(R.id.driver_name);
            modelName=itemView.findViewById(R.id.model_name);
            pricePerKilometer=itemView.findViewById(R.id.price_km);
            vehicleType=itemView.findViewById(R.id.gari_type);
            driverCity=itemView.findViewById(R.id.driver_city);
            }

        @Override
        public void onClick(View view) {
            Log.i("TAG","Lala item click hoa");
            int position=getAdapterPosition();
            Driver driver=list.get(position);
            Intent intent=new Intent(context, DriverProfileViewForPassenger.class);
            intent.putExtra("object",driver);
            context.startActivity(intent);
        }
    }

    }
