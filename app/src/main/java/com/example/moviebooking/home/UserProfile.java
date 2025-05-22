package com.example.moviebooking.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.UserInfo;

public class UserProfile extends AppCompatActivity {

    private UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getIntentData();
        setUserData();
        allViewClickListener();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
    }

    private void setUserData(){
        ImageView userProfilePicture = findViewById(R.id.user_profile_picture);
        TextView displayNameProfile = findViewById(R.id.user_profile_name);
        TextView usernameProfile = findViewById(R.id.user_profile_username);

        userProfilePicture.setImageResource(R.drawable.icon_user_ava);
        displayNameProfile.setText(userInfo.getName());
        usernameProfile.setText("@" + userInfo.getUsername());
    }

    private void allViewClickListener(){
        Button backButton = findViewById(R.id.user_profile_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button editProfileButton = findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, EditUserProfile.class);
                intent.putExtra("userInfoIntent",userInfo);
                startActivity(intent);
            }
        });
    }

}
