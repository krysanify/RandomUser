package com.krysanify.lib;

import android.content.Context;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static androidx.room.OnConflictStrategy.REPLACE;
import static java.util.Collections.emptyList;

/**
 * REQ01: Core functionality
 */
@SuppressWarnings("unused")
public class UserGen {
    private static final UserGen INSTANCE = new UserGen();
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
     * REQ06 encrypt {@link User#email} for local storage
     * @param user user info with plain email.
     */
    public void insert(@NonNull User user) {
        userDao().insert(user.encrypt());
        user.decrypt();// decrypt email to preserve state
    }

    /**
     * Delete given user from local storage
     * @param user user info to delete
     */
    public void delete(@NonNull User user) {
        userDao().delete(user);
    }

    /**
     * Delete all users from local storage
     */
    public void clear() {
        userDao().deleteAll();
    }

    /**
     * @param seed a random seed
     * @return a specific user with the given seed from web service
     */
    public User getBySeed(@NonNull String seed) {
        Call<ServiceBody> call = service().getBySeed(seed);
        List<User> users = processBody(call);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Asynchronously return a user with the given seed
     * @param seed a random seed
     * @param callback an instance of {@link UserGen.Callback}
     */
    public void getBySeed(@Gender String seed, @NonNull Callback callback) {
        Call<ServiceBody> call = service().getBySeed(seed);
        call.enqueue(new QueueCall(callback));
    }

    /**
     * @param gender either "male" or "female"
     * @return a user with the given gender from web service
     */
    public User getByGender(@Gender String gender) {
        Call<ServiceBody> call = service().getByGender(gender.toLowerCase());
        List<User> users = processBody(call);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Asynchronously return a user with given gender
     * @param gender either "male" or "female"
     * @param callback an instance of {@link UserGen.Callback}
     */
    public void getByGender(@Gender String gender, @NonNull Callback callback) {
        Call<ServiceBody> call = service().getByGender(gender.toLowerCase());
        call.enqueue(new QueueCall(callback));
    }

    /**
     * @param limit amount of generated users for up to 5000
     * @return users up to the given limit from web service
     */
    public List<User> getList(@IntRange(from = 1, to = 5000) int limit) {
        Call<ServiceBody> call = service().getList(limit);
        return processBody(call);
    }

    /**
     * Asynchronously return users up to the given limit
     * @param limit amount of generated users for up to 5000
     * @param callback an instance of {@link UserGen.Callback}
     */
    public void getList(@IntRange(from = 1, to = 5000) int limit,
                              @NonNull Callback callback) {
        Call<ServiceBody> call = service().getList(limit);
        call.enqueue(new QueueCall(callback));
    }

    /**
     * @param page zero-based multiplier to skip users
     * @param count maximum amount of users
     * @return users up to {@code count} on the given {@code page}
     */
    public List<User> getLocal(int page, int count) {
        int skip = 0 == page ? 0 : page * count;
        List<User> list = userDao().getList(skip, count);
        for (User user : list) {
            user.decrypt();
        }
        return list;
    }

    private List<User> processBody(Call<ServiceBody> call) {
        Response<ServiceBody> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }

        ServiceBody body = response.body();
        if (null == body) return emptyList();

        ServiceBody.Info info = body.getInfo();
        List<ServiceBody.Result> results = body.getResults();
        List<User> users = new ArrayList<>(results.size());
        for (ServiceBody.Result result : results) {
            User user = result.toUser(info.getSeed());
            users.add(user);
        }
        return users;
    }

    /**
     * @return web service client for randomuser.me API.
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
     * @return dao client for locally stored users.
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
     * Callback interface to received users from web service
     */
    public interface Callback {
        /**
         * @param users generated users, or empty
         */
        void onGenerated(List<User> users);
    }

    /**
     * REQ04: Get user info from <a href="https://randomuser.me/documentation">randomuser.me</a>.
     */
    public interface Service {
        /**
         * Returns a specific user with a random seed.
         *
         * @param seed random seed
         */
        @GET("?inc=name,gender,age,dob,email")
        Call<ServiceBody> getBySeed(@Query("seed") String seed);

        /**
         * Returns a random user with a specific gender.
         *
         * @param gender Gender user to return (Male or Female)
         */
        @GET("?inc=name,gender,age,dob,email")
        Call<ServiceBody> getByGender(@Gender @Query("gender") String gender);

        /**
         * Returns a list of random users up to 5000
         *
         * @param limit Number of users
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
         * @param seed random seed
         */
        @androidx.room.Query("SELECT * FROM users WHERE seed = :seed")
        User getBySeed(String seed);

        /**
         * Fetch a stored user with a specific gender.
         *
         * @param gender Gender user to return (male or female)
         */
        @androidx.room.Query("SELECT * FROM users WHERE gender = :gender LIMIT 1")
        User getByGender(@Gender String gender);

        /**
         * Fetch a list of stored users up to 5000
         *
         * @param count Number of users
         */
        @androidx.room.Query("SELECT * FROM users LIMIT :skip, :count")
        List<User> getList(int skip, @IntRange(from = 1, to = 5000) int count);

        @androidx.room.Query("DELETE FROM users")
        void deleteAll();

        @Insert(onConflict = REPLACE)
        void insert(User user);

        @Delete
        void delete(User user);
    }

    private class QueueCall implements retrofit2.Callback<ServiceBody> {
        private WeakReference<Callback> callbackRef;

        QueueCall(Callback callback) {
            callbackRef = new WeakReference<>(callback);
        }

        @Override
        public void onResponse(@NonNull Call<ServiceBody> call,
                               @NonNull Response<ServiceBody> response) {
            Callback callback = callbackRef.get();
            callbackRef.clear();

            if (null == callback) return;
            ServiceBody body = response.body();
            List<User> users = emptyList();

            if (null != body) {
                ServiceBody.Info info = body.getInfo();
                List<ServiceBody.Result> results = body.getResults();

                users = new ArrayList<>(results.size());
                for (ServiceBody.Result result : results) {
                    User user = result.toUser(info.getSeed());
                    users.add(user);
                }
            }

            callback.onGenerated(users);
            callbackRef = null;
        }

        @Override
        public void onFailure(@NonNull Call<ServiceBody> call, @NonNull Throwable t) {
            t.printStackTrace();
            callbackRef = null;
        }
    }
}
