package com.krysanify.lib;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

class ServiceBody {
    private Info info;
    private List<Result> results;

    @NonNull
    public Info getInfo() {
        return info;
    }

    @NonNull
    public List<Result> getResults() {
        return results;
    }

    static class Info {
        private int results;
        private int page;
        private String seed;
        private String version;

        @NonNull
        public String getSeed() {
            return seed;
        }
    }

    static class Result {
        private Name name;
        private String gender;
        private Dob dob;
        private String email;

        @NonNull
        public User toUser(@NonNull String seed) {
            long uid = Arrays.hashCode(new Object[]{seed, name, gender, dob, email});
            String encrypted = CryptoUtil.encrypt(email); // REQ06
            return new User(uid, seed, name.asWhole(), gender, dob.date, encrypted, dob.age);
        }
    }

    static class Name {
        private String title;
        private String first;
        private String last;

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "%s %s %s", title, first, last);
        }

        @NonNull
        public String asWhole() {
            return String.format(Locale.ENGLISH, "%s %s", first, last);
        }
    }

    static class Dob {
        private String date;
        private int age;
    }
}
