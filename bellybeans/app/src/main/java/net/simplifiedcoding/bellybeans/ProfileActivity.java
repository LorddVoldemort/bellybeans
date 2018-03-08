package net.simplifiedcoding.bellybeans;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    ImageView imageView;

    EditText editTextName;
    EditText editTextAddress;
    EditText editTextPhoneNumber;
    EditText editTextEmail;
    EditText editTextAccNum;
    EditText editTextCardNum;

    Uri uriProfileImage;
    String profileImageUrl;

    ProgressBar progressBar;

    FirebaseAuth mAuth;

    FirebaseDatabase database;

    DatabaseReference myRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editTextName=findViewById(R.id.Name);
        editTextAddress=findViewById(R.id.Address);
        editTextPhoneNumber=findViewById(R.id.PhoneNumber);
        editTextEmail=findViewById(R.id.Email);
        editTextAccNum=findViewById(R.id.AccNum);
        editTextCardNum=findViewById(R.id.CardNum);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User Information");



        mAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView);

        progressBar= findViewById(R.id.progressbar);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });


        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() ==null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }



    private void saveUserInformation() {
        String Name = editTextName.getText().toString();
        String Address = editTextAddress.getText().toString();
        String PhoneNumber = editTextPhoneNumber.getText().toString();
        String Email=editTextEmail.getText().toString();
        String AccNum=editTextAccNum.getText().toString();
        String CardNum=editTextCardNum.getText().toString();



        if (!TextUtils.isEmpty(Name) || !TextUtils.isEmpty(Address)|| !TextUtils.isEmpty(PhoneNumber)){
            String ID=myRef.push().getKey();

            UserInformation userInformation=new UserInformation(ID,Name,Address,PhoneNumber,Email,AccNum,CardNum);

            myRef.child(ID).setValue(userInformation);


        }
        else
        {
            Toast.makeText(ProfileActivity.this,"Field Required",Toast.LENGTH_SHORT).show();

        }



        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(Name)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage(){
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),CHOOSE_IMAGE);
    }
}
