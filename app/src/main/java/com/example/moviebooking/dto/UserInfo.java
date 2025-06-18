package com.example.moviebooking.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
    private String username;
    private String password;
    private String name;
    private String email;
    private String profilePic;
    private boolean role; // true = admin, false = user

    private static final String wrongPassword = "WP";
    private static final String wrongID = "WI";
    private static final String loginSuccess = "LS";

    private UserInfo() {
    }

    public UserInfo(String name, String username, String password) {
        this.username = username;
        this.email = username;
        this.password = password;
        this.name = name;
        this.role = false; // mặc định là user
    }

    public UserInfo(String UserInfoID, String password) {
        this.username = UserInfoID;
        this.password = password;
        this.role = false;
    }

    public UserInfo(String name, String username, String password, String profilePic) {
        this.name = name;
        this.username = username;
        this.email = username;
        this.password = password;
        this.profilePic = profilePic;
        this.role = false;
    }

    public UserInfo(String name, String username, String password, String profilePic, boolean role) {
        this.name = name;
        this.username = username;
        this.email = username;
        this.password = password;
        this.profilePic = profilePic;
        this.role = role;
    }

    public String isValidLogin(String UserInfoID, String password) {
        if (!this.username.equals(UserInfoID)) {
            return wrongID;
        }
        if (!this.password.equals(password)) {
            return wrongPassword;
        }
        return loginSuccess;
    }

    public void parseDataFromString(ArrayList<String> data) {
        this.username = data.get(0);
        this.password = data.get(1);
        this.name = data.get(2);
        this.email = data.get(3);
        if (data.size() > 4) {
            this.profilePic = data.get(4);
        }
        if (data.size() > 5) {
            this.role = Boolean.parseBoolean(data.get(5));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.email = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isAdmin() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }
}
