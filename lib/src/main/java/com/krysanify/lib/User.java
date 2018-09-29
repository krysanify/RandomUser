package com.krysanify.lib;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * User class
 */
@Entity(tableName = "users")
public class User implements Parcelable {
    @PrimaryKey
    public final long uid;
    public final String seed;
    public final String name;
    public final String gender;
    public final String dob;
    private String email;
    private int age;

    /**
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
        return age;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @return this {@link User} with a decrypted email
     */
    User decrypt() {
        email = Strings.isBlank(email) ? email : CryptoUtil.decrypt(email);
        return this;
    }

    /**
     * @return this {@link User} with an encrypted email
     */
    User encrypt() {
        email = Strings.isBlank(email) ? email : CryptoUtil.encrypt(email);
        return this;
    }

    @Override
    public int describeContents() {
        return (int) uid;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(seed);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(dob);
        dest.writeString(email);
        dest.writeInt(age);
    }

    @Ignore
    protected User(Parcel in) {
        uid = in.readLong();
        seed = in.readString();
        name = in.readString();
        gender = in.readString();
        dob = in.readString();
        email = in.readString();
        age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
