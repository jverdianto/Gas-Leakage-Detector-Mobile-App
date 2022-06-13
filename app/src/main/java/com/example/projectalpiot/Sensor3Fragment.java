package com.example.projectalpiot;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Use the {@link Sensor3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sensor3Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sensor3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sensor3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Sensor3Fragment newInstance(String param1, String param2) {
        Sensor3Fragment fragment = new Sensor3Fragment();
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
        return inflater.inflate(R.layout.fragment_sensor3, container, false);
    }

    Switch switchFan3;
    Button button3;
    TextView textViewgasLeakage3;
    ImageView greenLED3;
    ImageView redLED3;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHFAN3 = "switchfan3";

    private String switchOnOff;
    public boolean manualFan3 = false;
    public boolean manualFan3a = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchFan3 = getView().findViewById(R.id.switchFan3);
        button3 = getView().findViewById(R.id.button3);
        textViewgasLeakage3 = getView().findViewById(R.id.textViewgasLeakage3);
        greenLED3 = getView().findViewById(R.id.greenLED3);
        redLED3 = getView().findViewById(R.id.redLED3);

        DAOSensor dao = new DAOSensor();

        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwHBZwF0AIUDLjR6r");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    textViewgasLeakage3.setText("GAS LEAKAGE DETECTED !!!");
                    redLED3.setVisibility(View.VISIBLE);
                    greenLED3.setVisibility(View.INVISIBLE);
                    if(manualFan3a == false){
                        switchFan3.setChecked(true);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan3.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwHBZwF0AIUDLjR6r", hashMap);
                }else{
                    textViewgasLeakage3.setText("No Gas Leakage Detected");
                    redLED3.setVisibility(View.INVISIBLE);
                    greenLED3.setVisibility(View.VISIBLE);
                    manualFan3a = false;
                    if(manualFan3 == false){
                        switchFan3.setChecked(false);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan3.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwHBZwF0AIUDLjR6r", hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewgasLeakage3.setText("error");
            }
        });

        button3.setOnClickListener(v->{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(switchFan3.isChecked()) {
                manualFan3 = true;
                hashMap.put("fan", true);
            }else{
                manualFan3a = true;
                hashMap.put("fan", false);
            }
            dao.update("-N3hwHBZwF0AIUDLjR6r", hashMap).addOnSuccessListener(suc->{
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

        editor.putString(SWITCHFAN3, String.valueOf(switchFan3.isChecked()));

        editor.apply();

        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getString(SWITCHFAN3, "false");
    }

    public void updateViews() {
        switchFan3.setChecked(Boolean.parseBoolean(switchOnOff));
    }
}