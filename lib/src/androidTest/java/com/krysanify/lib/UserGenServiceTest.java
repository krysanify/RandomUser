package com.krysanify.lib;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * REQ03: Test cases for {@link UserGen.Service} implementation
 */
@RunWith(AndroidJUnit4.class)
public class UserGenServiceTest {

    private UserGen.Dao dao;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        dao = UserGen.init(appContext).userDao();
    }

    /**
     * Given an email address,
     * when it's encrypted,
     * then assert email address can be decrypted
     */
    @Test
    public void email_encrypted() {
        String encrypted = CryptoUtil.encrypt("rolf.hegdal@example.com");
        String email = CryptoUtil.decrypt(encrypted);
        assertEquals("rolf.hegdal@example.com", email);
    }

    /**
     * Given a random user with seed "qwerty" from web service,
     * when inserted to local storage,
     * then assert user can be retrieved and deleted at the end.
     */
    @Test
    public void service_bySeed() {
        Call<ServiceBody> call = UserGen.get().service().getBySeed("qwerty");
        Response<ServiceBody> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(response);
        ServiceBody.Info info = response.body().getInfo();
        List<ServiceBody.Result> results = response.body().getResults();

        for (ServiceBody.Result result : results) {
            dao.insert(result.toUser(info.getSeed()));
        }

        User user = dao.getBySeed("qwerty");
        assertNotNull(user);
        assertEquals(info.getSeed(), user.seed);
        dao.delete(user);
    }

    /**
     * Given a random user with gender "female" from web service,
     * when inserted to local storage,
     * then assert user can be retrieved and deleted at the end.
     */
    @Test
    public void service_byGender() {
        Call<ServiceBody> call = UserGen.get().service().getByGender("female");
        Response<ServiceBody> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(response);
        ServiceBody.Info info = response.body().getInfo();
        List<ServiceBody.Result> results = response.body().getResults();

        User user = null;
        for (ServiceBody.Result result : results) {
            user = result.toUser(info.getSeed());
            dao.insert(user);
        }

        User female = dao.getByGender("female");
        assertNotNull(user);
        assertNotNull(female);
        assertEquals(user.name, female.name);
        dao.delete(female);
    }

    /**
     * Given five random users from web service,
     * when inserted to local storage,
     * then assert users can be retrieved and deleted at the end.
     */
    @Test
    public void service_getList() {
        int limit = 5;
        Call<ServiceBody> call = UserGen.get().service().getList(limit);
        Response<ServiceBody> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(response);
        ServiceBody.Info info = response.body().getInfo();
        List<ServiceBody.Result> results = response.body().getResults();
        assertEquals(limit, results.size());

        List<User> list = dao.getList(limit);
        assertEquals(0, list.size());

        for (ServiceBody.Result result : results) {
            dao.insert(result.toUser(info.getSeed()));
        }

        list = dao.getList(limit);
        assertNotNull(list);
        assertEquals(limit, list.size());
        dao.deleteAll();
    }
}