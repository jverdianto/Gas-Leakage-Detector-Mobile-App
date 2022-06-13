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
 * Use the {@link Sensor2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sensor2Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sensor2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sensor2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Sensor2Fragment newInstance(String param1, String param2) {
        Sensor2Fragment fragment = new Sensor2Fragment();
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
        return inflater.inflate(R.layout.fragment_sensor2, container, false);
    }

    Switch switchFan2;
    Button button2;
    TextView textViewgasLeakage2;
    ImageView greenLED2;
    ImageView redLED2;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHFAN2 = "switchfan2";

    private String switchOnOff;
    public boolean manualFan2 = false;
    public boolean manualFan2a = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchFan2 = getView().findViewById(R.id.switchFan2);
        button2 = getView().findViewById(R.id.button2);
        textViewgasLeakage2 = getView().findViewById(R.id.textViewgasLeakage2);
        greenLED2 = getView().findViewById(R.id.greenLED2);
        redLED2 = getView().findViewById(R.id.redLED2);

        DAOSensor dao = new DAOSensor();

        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwGZtqtnoXThTyvPq");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    textViewgasLeakage2.setText("GAS LEAKAGE DETECTED !!!");
                    redLED2.setVisibility(View.VISIBLE);
                    greenLED2.setVisibility(View.INVISIBLE);
                    if(manualFan2a == false){
                        switchFan2.setChecked(true);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan2.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwGZtqtnoXThTyvPq", hashMap);
                }else{
                    textViewgasLeakage2.setText("No Gas Leakage Detected");
                    redLED2.setVisibility(View.INVISIBLE);
                    greenLED2.setVisibility(View.VISIBLE);
                    manualFan2a = false;
                    if(manualFan2 == false){
                        switchFan2.setChecked(false);
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if(switchFan2.isChecked()) {
                        hashMap.put("fan", true);
                    }else{
                        hashMap.put("fan", false);
                    }
                    dao.update("-N3hwGZtqtnoXThTyvPq", hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewgasLeakage2.setText("error");
            }
        });

        button2.setOnClickListener(v->{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(switchFan2.isChecked()) {
                manualFan2 = true;
                hashMap.put("fan", true);
            }else{
                manualFan2a = true;
                hashMap.put("fan", false);
            }
            dao.update("-N3hwGZtqtnoXThTyvPq", hashMap).addOnSuccessListener(suc->{
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

        editor.putString(SWITCHFAN2, String.valueOf(switchFan2.isChecked()));

        editor.apply();

        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getString(SWITCHFAN2, "false");
    }

    public void updateViews() {
        switchFan2.setChecked(Boolean.parseBoolean(switchOnOff));
    }
}