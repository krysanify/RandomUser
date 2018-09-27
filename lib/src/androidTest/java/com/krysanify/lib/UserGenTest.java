package com.krysanify.lib;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UserGenTest {
    private UserGen userGen;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        userGen = UserGen.init(appContext);
    }

    @After
    public void tearDown() {
        userGen.userDao().deleteAll();
    }

    /**
     * Given a random user with seed "decaffed" either from local storage or web service,
     * when retrieved, then assert its seed is "decaffed"
     */
    @Test
    public void usergen_bySeed() {
        User decaffed = null;
        try {
            decaffed = userGen.getBySeed("decaffed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(decaffed);
        assertEquals("decaffed", decaffed.seed);
    }

    /**
     * Given a random user with gender "female" either from local storage or web service,
     * when retrieved, then assert its gender is "female"
     */
    @Test
    public void usergen_isFemale() {
        User female = null;
        try {
            female = userGen.getByGender("female");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(female);
        assertEquals("female", female.gender);
    }

    /**
     * Given a random user with gender "unknown" either from local storage or web service,
     * when retrieved, then assert its gender is NOT "unknown"
     */
    @Test
    public void usergen_notUnknown() {
        User unknown = null;
        try {
            unknown = userGen.getByGender("unknown");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(unknown);
        assertNotEquals("unknown", unknown.gender);
    }

    /**
     * Given five random users either from local storage or web service,
     * when retrieved, then assert there are five users
     */
    @Test
    public void usergen_getFive() {
        List<User> users = null;
        try {
            users = userGen.getList(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(5, users.size());
    }
}
