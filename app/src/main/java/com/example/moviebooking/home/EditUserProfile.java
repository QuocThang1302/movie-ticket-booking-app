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
        ImageView editProfileImage = findViewById(R.id.edit_user_profile_picture);
        EditText editDisplayname = findViewById(R.id.et_display_name);

        editDisplayname.setText(userInfo.getName());
        editProfileImage.setImageResource(R.drawable.icon_user_ava);
    }

    private void allViewClickListener(){
        Button editprofileBackButton = findViewById(R.id.edit_user_profile_back_button);
        editprofileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
