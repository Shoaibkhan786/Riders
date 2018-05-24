package com.example.muhammadkhan.ridersapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadkhan.ridersapp.Activities.Splash;
import com.example.muhammadkhan.ridersapp.ListAdapter;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavigationDrawerTopFragment extends Fragment {

    private ListView listViewTop;
    Context context;
    FrameLayout layout;
    DrawerLayout drawerLayout;
    String [] titles=null;
    Integer [] icons=null;
    private FragmentTransaction transaction;

    public void setAttributes(DrawerLayout drawerLayout, FrameLayout layout){
        this.drawerLayout=drawerLayout;
        this.layout=layout;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=getActivity();

        titles= new String[]{
                "MotorBike",
                "Bus",
                "Car",
                "Rikhshaw",
                "QINGQI",
                "Van",
                "SazukiBolan"
        };
        icons= new Integer[]{
                R.mipmap.ic_motorcycle_black_24dp,
                R.mipmap.ic_directions_bus_black_24dp,
                R.mipmap.ic_local_taxi_black_24dp,
                R.mipmap.rikshaw,
                R.mipmap.qinjqi,
                R.mipmap.van,
                R.mipmap.bolan_sazuki
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        listViewTop=(ListView) view.findViewById(R.id.top_list);
        ListAdapter adapter=new ListAdapter(context,titles,icons);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String currentCity = preferences.getString("current_city", "");
        listViewTop.setAdapter(adapter);
        listViewTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView textView=(TextView) view.findViewById(R.id.title);
                Log.i("TAG",textView.getText().toString());
                drawerLayout.closeDrawers();
                //back ground thread
                Handler handler=new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Drivers").child(currentCity);
                        System.out.println(reference);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(textView.getText().toString())){
                                    //if data exists
                                    ParticularVehicleSearch vehicleSearch=new ParticularVehicleSearch();
                                    vehicleSearch.setVehicle(textView.getText().toString());
                                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame,vehicleSearch).commit();
                             }else {
                                    SearchEmptyFragment searchEmptyFragment=new SearchEmptyFragment();
                                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame,searchEmptyFragment).commit();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                           }
                        });
                    }
                });
            }
        });
        return view;
    }
}
