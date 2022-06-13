package com.example.projectalpiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.projectalpiot.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Switch switchBuzzer;
    Button saveButton;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHBUZZER = "switchbuzzer";

    private String switchOnOff;
    private int gasleak1 = 0;
    private int gasleak2 = 0;
    private int gasleak3 = 0;
    private boolean manualBuzzer = false;
    private boolean manualBuzzerA = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Sensor1Fragment());

        switchBuzzer = findViewById(R.id.switchBuzzer);
        saveButton = findViewById(R.id.saveButton);

        DAOSensor dao = new DAOSensor();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch(item.getItemId()){
                case R.id.sensor1:
                    replaceFragment(new Sensor1Fragment());
                    break;
                case R.id.sensor2:
                    replaceFragment(new Sensor2Fragment());
                    break;
                case R.id.sensor3:
                    replaceFragment(new Sensor3Fragment());
                    break;
            }

            return true;
        });

        DatabaseReference database1 = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwFx1OI_N_h9FTILk");

        database1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    switchBuzzer.setChecked(true);
                    gasleak1 = 1;
                    manualBuzzer = false;
                    if(manualBuzzerA == false){
                        switchBuzzer.setChecked(true);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My Notification");
                    builder.setContentTitle("GAS LEAKAGE DETECTED");
                    builder.setContentText("Sensor 1 detected a gas leakage! Hurry up and check it!");
                    builder.setSmallIcon(R.drawable.ic_baseline_local_gas_station_24);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());

                }else{
                    gasleak1 = 0;
                    manualBuzzerA = false;
                    if(manualBuzzer == false){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(gasleak1 == 0 && gasleak2 == 0 && gasleak3 == 0){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference database2 = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwGZtqtnoXThTyvPq");

        database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    switchBuzzer.setChecked(true);
                    gasleak2 = 1;
                    if(manualBuzzerA == false){
                        switchBuzzer.setChecked(true);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My Notification");
                    builder.setContentTitle("GAS LEAKAGE DETECTED");
                    builder.setContentText("Sensor 2 detected a gas leakage! Hurry up and check it!");
                    builder.setSmallIcon(R.drawable.ic_baseline_local_gas_station_24);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());

                }else{
                    gasleak2 = 0;
                    if(gasleak1 == 0 && gasleak2 == 0 && gasleak3 == 0){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(manualBuzzer == false){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference database3 = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3hwHBZwF0AIUDLjR6r");

        database3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean gasLeak = (Boolean) dataSnapshot.child("gasLeak").getValue();
                if (gasLeak == true) {
                    switchBuzzer.setChecked(true);
                    gasleak3 = 1;
                    if(manualBuzzerA == false){
                        switchBuzzer.setChecked(true);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My Notification");
                    builder.setContentTitle("GAS LEAKAGE DETECTED");
                    builder.setContentText("Sensor 3 detected a gas leakage! Hurry up and check it!");
                    builder.setSmallIcon(R.drawable.ic_baseline_local_gas_station_24);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());

                }else{
                    gasleak3 = 0;
                    if(gasleak1 == 0 && gasleak2 == 0 && gasleak3 == 0){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(manualBuzzer == false){
                        switchBuzzer.setChecked(false);
                        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

                        database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if (switchBuzzer.isChecked()) {
                                    hashMap.put("buzzer", true);
                                } else {
                                    hashMap.put("buzzer", false);
                                }
                                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Sensor").child("-N3i9sXO29PqmYzVC3Xr");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> hashMap = new HashMap<>();
                if (switchBuzzer.isChecked()) {
                    hashMap.put("buzzer", true);
                } else {
                    hashMap.put("buzzer", false);
                }
                dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveButton.setOnClickListener(v->{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(switchBuzzer.isChecked()) {
                manualBuzzer = true;
                hashMap.put("buzzer", true);
            }else{
                manualBuzzerA = true;
                hashMap.put("buzzer", false);
            }
            dao.update("-N3i9sXO29PqmYzVC3Xr", hashMap).addOnSuccessListener(suc->{
                Toast.makeText(this, "Sensor updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(er->{
                Toast.makeText(this, er.getMessage(), Toast.LENGTH_SHORT).show();
            });

            saveData();

        });

        loadData();
        updateViews();

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SWITCHBUZZER, String.valueOf(switchBuzzer.isChecked()));

        editor.apply();

        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getString(SWITCHBUZZER, "false");
    }

    public void updateViews() {
        switchBuzzer.setChecked(Boolean.parseBoolean(switchOnOff));
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}