package com.example.moviebooking.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.UserInfo;

public class EditUserProfile extends AppCompatActivity {

    private UserInfo userInfo = null;
    private boolean isUpdate = false;
    EditText editDisplayname;
    ImageView editProfileImage;
    Button saveChangedButton;
    Button editprofileBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        getIntentData();
        setUserInfo();
        allViewClickListener();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userInfoIntent");
    }

    private void setUserInfo(){
        editProfileImage = findViewById(R.id.edit_user_profile_picture);
        editDisplayname = findViewById(R.id.et_display_name);

        editDisplayname.setText(userInfo.getName());
        editProfileImage.setImageResource(R.drawable.icon_user_ava);
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
                userInfo.setName(editDisplayname.getText().toString());
                isUpdate = true;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("UpdateStat", isUpdate);
                resultIntent.putExtra("UserInfoIntent", userInfo);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }

}
