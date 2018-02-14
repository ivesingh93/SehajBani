package com.sehaj.bani.rest.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ivesingh on 1/3/18.
 */

public class UserCredentials {

    @SerializedName("first_name")
    private String first_name;

    @SerializedName("last_name")
    private String last_name;

    @SerializedName("account_id")
    private String account_id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("message")
    private String message;

    public UserCredentials(String first_name, String last_name, String account_id, String username, String password){
        this.first_name = first_name;
        this.last_name = last_name;
        this.account_id = account_id;
        this.username = username;
        this.password = password;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getAccountID() {
        return account_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMessage() {
        return message;
    }

}
