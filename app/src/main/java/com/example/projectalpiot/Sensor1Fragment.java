package com.example.projectalpiot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sensor1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sensor1Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sensor1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sensor1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Sensor1Fragment newInstance(String param1, String param2) {
        Sensor1Fragment fragment = new Sensor1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor1, container, false);
    }

    Switch switchFan1;
    Button button1;
    TextView textViewgasLeakage1;
    ImageView greenLED1;
    ImageView redLED1;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHFAN1 = "switchfan1";

    private String switchOnOff;
    public boolean manualFan1 = false;
    public boolean manualFan1a = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchFan1 = getView().findViewById(R.id.switchFan1);
        button1 = getView().findViewById(R.id.button1);
        textViewgasLeakage1 = getView().findViewById(R.id.textViewgasLeakage1);
        greenLED1 = getView().findViewById(R.id.greenLED1);
        redLED1 = getView().findViewById(R.id.redLED1);

        DAOSensor dao = new DAOSensor();

        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwFx1OI_N_h9FTILk");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    textViewgasLeakage1.setText("GAS LEAKAGE DETECTED !!!");
                    redLED1.setVisibility(View.VISIBLE);
                    greenLED1.setVisibility(View.INVISIBLE);
                    manualFan1 = false;
                    if(manualFan1a == false){
                        switchFan1.setChecked(true);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan1.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwFx1OI_N_h9FTILk", hashMap);
                }else{
                    textViewgasLeakage1.setText("No Gas Leakage Detected");
                    redLED1.setVisibility(View.INVISIBLE);
                    greenLED1.setVisibility(View.VISIBLE);
                    manualFan1a = false;
                    if(manualFan1 == false){
                        switchFan1.setChecked(false);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan1.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwFx1OI_N_h9FTILk", hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewgasLeakage1.setText("error");
            }
        });

        button1.setOnClickListener(v->{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(switchFan1.isChecked()) {
                manualFan1 = true;
                hashMap.put("fan", true);
            }else{
                manualFan1a = true;
                hashMap.put("fan", false);
            }
            dao.update("-N3hwFx1OI_N_h9FTILk", hashMap).addOnSuccessListener(suc->{
                Toast.makeText(getActivity(), "Sensor updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(er->{
                Toast.makeText(getActivity(), er.getMessage(), Toast.LENGTH_SHORT).show();
            });

            saveData();

        });

        loadData();
        updateViews();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SWITCHFAN1, String.valueOf(switchFan1.isChecked()));

        editor.apply();

        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getString(SWITCHFAN1, "false");
    }

    public void updateViews() {
        switchFan1.setChecked(Boolean.parseBoolean(switchOnOff));
    }
}