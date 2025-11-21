package com.example.eecs4443groupprojectgroup1.User;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

@Entity(tableName = "users", indices = {@Index(value = {"username"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "username")
    public String username;

    public String password;
    public String email;
    public String dateOfBirth;
    public String description;
    public String userIcon; //image
    public String gender;
}