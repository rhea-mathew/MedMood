package com.example.medmood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Journal extends AppCompatActivity {

    private static final String KEY1 = "title";
    private static final String KEY2 = "journalEntry";
    private static final String KEY3 = "timestamp";
    private static final String TAG = "Journal Data";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCollection = db.collection("journal");

    }

    public void submit(View view) {
        EditText text1 = findViewById(R.id.editText1);
        EditText text2 = findViewById(R.id.editText2);
        String title = text1.getText().toString().trim();
        String journalEntry = text2.getText().toString().trim();

        if (title.isEmpty() || journalEntry.isEmpty()) {
            return;
        }

        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
        String timestamp = date.format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put(KEY1, title);
        data.put(KEY2, journalEntry);
        data.put(KEY3, timestamp);

        myCollection.add(data) //replaces old stuff
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(Journal.this, "Journal entry added!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(Journal.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        db.collection("journal")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}