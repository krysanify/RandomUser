package com.krysanify.lib;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    public final long uid;
    public final String seed;
    public final String name;
    public final String gender;
    public final String dob;
    public final String email;
    private int age;

    /**
     * User info
     * @param uid hashcode value of seed, name, gender, dob, and email
     * @param seed taken from {@link ServiceBody.Info#seed}
     * @param name taken from {@link ServiceBody.Name#asWhole}
     * @param gender taken from {@link ServiceBody.Result#gender}
     * @param dob taken from {@link ServiceBody.Dob#date}
     * @param email encrypted by {@link CryptoUtil#encrypt(String)} from {@link ServiceBody.Result#email}
     * @param age taken from {@link ServiceBody.Dob#age}
     */
    User(long uid, @NonNull String seed, String name, @Gender String gender, String dob,
         String email, int age) {
        this.uid = uid;
        this.seed = seed;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.age = age;
    }

    public int getAge() {
        if (0 > age) {
            age = 42;//fixme: must calculate from dob when -1
        }
        return age;
    }

    public String email() {
        return CryptoUtil.decrypt(email);
    }
}
