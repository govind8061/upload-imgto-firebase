package com.example.firebasefirestoreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentDelete;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_IMAGE =101 ;
    FirebaseDatabase db;
    DatabaseReference dbref;
    StorageReference stRef;
    EditText etImageName;
    Button btnUpload;
    ImageView imgUpload;
    Uri imgUri;
    boolean isImageAdded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firestore database
        db= FirebaseDatabase.getInstance();
        dbref=db.getReference().child("exlist");
        stRef= FirebaseStorage.getInstance().getReference().child("images");


        //edittexts
        etImageName=findViewById(R.id.etImgageName);


        //textviews

        //buttons
        btnUpload=findViewById(R.id.btnUpload);

        //imageviews
        imgUpload=findViewById(R.id.imgUpload);




        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQ_CODE_IMAGE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String imgName=etImageName.getText().toString();
                if (isImageAdded!=false && imgName!=null){
                    uploadImage(imgName);
                }
            }
        });



    }

    private void uploadImage(final String imgName) {

        final String key=dbref.push().getKey();
        stRef.child(key + ".jpg").putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                stRef.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap=new HashMap();
                        hashMap.put("image_name",imgName);
                        hashMap.put("uri",uri.toString());

                        dbref.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_CODE_IMAGE && data!=null){
            imgUri=data.getData();
            isImageAdded=true;
            imgUpload.setImageURI(imgUri);
        }
    }
}