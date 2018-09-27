package com.krysanify.lib;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

public class UserGen {
    private static UserGen INSTANCE = new UserGen();
    private Service service;
    private LocalDb local;

    private UserGen() {
    }

    public static UserGen get() {
        return INSTANCE;
    }

    public static UserGen init(@Nullable Context context) {
        if (null != context) INSTANCE.local(context);
        return INSTANCE;
    }

    /**
     * @param seed a random seed
     * @return a specific user with given seed from local storage (if stored) or web service
     * @throws IOException on error at {@link Call#execute()}
     */
    public User getBySeed(@NonNull String seed) throws IOException {
        Dao dao = userDao();
        User user = dao.getBySeed(seed);
        if (null != user) return user;

        ServiceBody body = service()
                .getBySeed(seed)
                .execute()
                .body();

        ServiceBody.Info info = body.getInfo();
        List<ServiceBody.Result> results = body.getResults();
        for (ServiceBody.Result result : results) {
            user = result.toUser(info.getSeed());
            dao.insert(user);
        }
        return user;
    }

    /**
     * @param gender either "male" or "female"
     * @return a user with given gender from local storage (if any) or web service
     * @throws IOException on error at {@link Call#execute()}
     */
    public User getByGender(@Gender String gender) throws IOException {
        Dao dao = userDao();
        User user = dao.getByGender(gender);
        if (null != user) return user;

        ServiceBody body = service()
                .getByGender(gender)
                .execute()
                .body();

        ServiceBody.Info info = body.getInfo();
        List<ServiceBody.Result> results = body.getResults();
        for (ServiceBody.Result result : results) {
            user = result.toUser(info.getSeed());
            dao.insert(user);
        }
        return user;
    }

    /**
     * @param limit amount of generated users for up to 5000
     * @return users up to given limit from local storage (if any) or web service
     * @throws IOException on error at {@link Call#execute()}
     */
    public List<User> getList(@IntRange(from = 1, to = 5000) int limit) throws IOException {
        Dao dao = userDao();
        List<User> users = dao.getList(limit);
        if (!users.isEmpty()) return users;

        ServiceBody body = service()
                .getList(limit)
                .execute()
                .body();

        ServiceBody.Info info = body.getInfo();
        List<ServiceBody.Result> results = body.getResults();
        users = new ArrayList<>(results.size());
        for (ServiceBody.Result result : results) {
            User user = result.toUser(info.getSeed());
            users.add(user);
            dao.insert(user);
        }
        return users;
    }

    /**
     * @return a private service, only exposed for testing purpose.
     */
    @NonNull
    UserGen.Service service() {
        if (null == service) {
            //todo: extract URL to Gradle build file or a .properties file
            service = new Retrofit.Builder()
                    .baseUrl("https://randomuser.me/api/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(Service.class);
        }
        return service;
    }

    /**
     * @return a private dao, only exposed for testing purpose.
     */
    @NonNull
    UserGen.Dao userDao() {
        if (null == local) throw new IllegalStateException("LocalDb not initialized");
        return local.userDao();
    }

    /**
     * Build a local database assuming db is used only for UserGen.
     *
     * @param context application context
     */
    private void local(@NonNull Context context) {
        if (null != local) return;
        context = context.getApplicationContext();
        local = Room.databaseBuilder(context, LocalDb.class, "randomUser-local")
                .build();
    }

    /**
     * Get user info from <a href="https://randomuser.me/documentation">randomuser.me</a>.
     */
    public interface Service {
        /**
         * Returns a specific user with a random seed.
         *
         * @param seed - random seed
         */
        @GET("?inc=name,gender,age,dob,email")
        Call<ServiceBody> getBySeed(@Query("seed") String seed);

        /**
         * Returns a random user with a specific gender.
         *
         * @param gender - Gender user to return (Male or Female)
         */
        @GET("?inc=name,gender,age,dob,email")
        Call<ServiceBody> getByGender(@Gender @Query("gender") String gender);

        /**
         * Returns a list of random users up to 5000
         *
         * @param limit - Number of users
         */
        @GET("?inc=name,gender,age,dob,email")
        Call<ServiceBody> getList(@IntRange(from = 1, to = 5000) @Query("results") int limit);

    }

    /**
     * REQ05: Store, retrieve, and delete user info from local storage
     */
    @androidx.room.Dao
    public interface Dao {
        /**
         * Fetch a specific user with a random seed.
         *
         * @param seed - random seed
         */
        @androidx.room.Query("SELECT * FROM users WHERE seed = :seed")
        User getBySeed(@NonNull String seed);

        /**
         * Fetch a stored user with a specific gender.
         *
         * @param gender - Gender user to return (Male or Fermale)
         */
        @androidx.room.Query("SELECT * FROM users WHERE gender = :gender LIMIT 1")
        User getByGender(@Gender String gender);

        /**
         * Fetch a list of stored users up to 5000
         *
         * @param limit - Number of users
         */
        @androidx.room.Query("SELECT * FROM users LIMIT :limit")
        List<User> getList(@IntRange(from = 1, to = 5000) int limit);

        @androidx.room.Query("DELETE FROM users")
        void deleteAll();

        @Insert(onConflict = REPLACE)
        void insert(@NonNull User user);

        @Delete
        void delete(@NonNull User user);
    }

}
