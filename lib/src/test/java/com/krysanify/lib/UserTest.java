package com.krysanify.lib;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserTest {
    /**
     * Given various values, when a new user is created,
     * then assert its properties are as they should.
     */
    @Test
    public void init_isCorrect() {
        User user = new User(1, "2da87e9305069f1d", "mr rolf hegdal", "male",
                "1975-11-12T06:34:44Z", "rolf.hegdal@example.com", 42);
        assertEquals("2da87e9305069f1d", user.seed);
        assertEquals("mr rolf hegdal", user.name);
        assertEquals("male", user.gender);
        assertEquals("1975-11-12T06:34:44Z", user.dob);
        assertEquals("rolf.hegdal@example.com", user.getEmail());
        assertEquals(42, user.getAge());
    }

    /**
     * Given a new user, when given age is 24, then assert user's age are 24.
     */
    @Test
    public void age_byInit() {
        User user = new User(1, "2da87e9305069f1d", "mr rolf hegdal", "male",
                "1975-11-12T06:34:44Z", "rolf.hegdal@example.com", 24);
        assertEquals(24, user.getAge());
    }

    @Ignore("until age can be calculated from dob")
    @Test
    public void age_byDob() {
        User user = new User(1, "2da87e9305069f1d", "mr rolf hegdal", "male",
                "1970-11-12T06:34:44Z", "rolf.hegdal@example.com", -1);
        assertEquals(47, user.getAge());
    }
}