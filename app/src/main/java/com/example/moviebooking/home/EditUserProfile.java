package com.example.moviebooking.home;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditUserProfile extends AppCompatActivity {

    private UserInfo userInfo = null;
    private boolean isUpdate = false;
    private Cloudinary cloudinary;
    private EditText editDisplayname;
    private ImageView editProfileImage;
    private Button saveChangedButton;
    private Button editprofileBackButton;
    private Button btnChangeImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        getIntentData();
        setupCloudinary();
        launcherResgister();
        setUserInfo();
        allViewClickListener();
    }
    private void setupCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "deagejli9");
        config.put("api_key", "121824264332777");
        config.put("api_secret", "ZLnXUrTDXDPyBSCWUdmUWmQgDOc");
        cloudinary = new Cloudinary(config);
    }
    private void getIntentData(){
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userInfoIntent");
        username = userInfo.getUsername();
    }

    private void setUserInfo(){
        editProfileImage = findViewById(R.id.edit_user_profile_picture);
        editDisplayname = findViewById(R.id.et_display_name);

        editDisplayname.setText(userInfo.getName());
        if (userInfo.getProfilePic() != null && !userInfo.getProfilePic().isEmpty()) {
            Glide.with(this).load(userInfo.getProfilePic()).apply(new RequestOptions()
                    .centerCrop()
                    .circleCrop()).into(editProfileImage);
        } else {
            editProfileImage.setImageResource(R.drawable.icon_user_ava);
        }

    }

    private void launcherResgister(){
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        editProfileImage.setImageURI(imageUri);
                        Log.d("UploadImage", "Image URI: " + imageUri.toString());

                        uploadImageToCloudinary(imageUri);
                    }
                }
        );
        btnChangeImage = findViewById(R.id.edit_profile_picture_button);
        btnChangeImage.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToCloudinary(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            new Thread(() -> {
                try {
                    Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
                    String imageUrl = uploadResult.get("secure_url").toString();

                    runOnUiThread(() -> {
                        updateProfilePictureInFirebase(imageUrl);
                        Log.d("CloudinaryUpload", "Upload thành công: " + imageUrl);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Log.e("CloudinaryUpload", "Upload thất bại", e);
                        Toast.makeText(this, "Upload ảnh thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        } catch (IOException e) {
            Log.e("CloudinaryUpload", "Lỗi xử lý ảnh", e);
            Toast.makeText(this, "Lỗi ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void updateUserNameInFirebase(String username, String newName, OnUpdateNameListener listener) {
        FireBaseManager.getInstance().getDatabaseReference()
                .child("USERS_INFO")
                .child(username)
                .child("name")
                .setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    listener.onUpdate(true);
                })
                .addOnFailureListener(e -> {
                    listener.onUpdate(false);
                });
    }

    public interface OnUpdateNameListener {
        void onUpdate(boolean success);
    }

    private void updateProfilePictureInFirebase(String imageUrl) {
        FireBaseManager.getInstance().updateProfilePicture(username, imageUrl, success -> {
            if (success) {
                Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                userInfo.setProfilePic(imageUrl);
                isUpdate = true;
            } else {
                Toast.makeText(this, "Cập nhật ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void allViewClickListener(){
        editprofileBackButton = findViewById(R.id.edit_user_profile_back_button);
        editprofileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveChangedButton = findViewById(R.id.edit_save_button);
        saveChangedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editDisplayname.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast.makeText(EditUserProfile.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                userInfo.setName(newName);
                updateUserNameInFirebase(username, newName, success -> {
                    if (success) {
                        Toast.makeText(EditUserProfile.this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
                        isUpdate = true;

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("UpdateStat", isUpdate);
                        resultIntent.putExtra("UserInfoIntent", userInfo);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(EditUserProfile.this, "Cập nhật tên thất bại, thử lại sau", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}


