package com.example.projectalpiot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOSensor {
    private DatabaseReference databaseReference;

    public DAOSensor(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://project-alp-iot-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference(Sensor.class.getSimpleName());
    }

    public Task<Void> add(Sensor sensor){
        return databaseReference.push().setValue(sensor);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

}
