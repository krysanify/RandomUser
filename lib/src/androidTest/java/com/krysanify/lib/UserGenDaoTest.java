package com.krysanify.lib;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * REQ03: Test cases for {@link UserGen.Dao} implementation
 */
@RunWith(AndroidJUnit4.class)
public class UserGenDaoTest {

    private UserGen.Dao dao;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        dao = UserGen.init(appContext).userDao();
    }

    /**
     * Given a missing user with seed "lorem" on local storage,
     * when inserted as new user,
     * then assert retrieved properties are correct and deleted at the end.
     */
    @Test
    public void dao_bySeed() {
        User lorem = dao.getBySeed("lorem");
        if (null == lorem) {
            lorem = new User(1, "lorem", "lorem ipsum", "female", "1976-12-03T00:00:00Z", "", -1);
            dao.insert(lorem.encrypt());
            lorem = dao.getBySeed("lorem");
        }

        assertEquals("lorem", lorem.seed);
        assertEquals("lorem ipsum", lorem.name);
        assertEquals("female", lorem.gender);
        assertEquals("1976-12-03T00:00:00Z", lorem.dob);
        assertEquals("", lorem.decrypt().getEmail());

        dao.delete(lorem);
        lorem = dao.getBySeed("lorem");
        assertNull(lorem);
    }

    /**
     * Given a missing user with gender "male" on local storage,
     * when inserted as new user,
     * then assert retrieved properties are correct and deleted at the end.
     */
    @Test
    public void dao_byGender() {
        User lorem = dao.getByGender("male");
        if (null == lorem) {
            lorem = new User(1, "lorem", "lorem ipsum", "male", "1976-12-03T00:00:00Z", "", -1);
            dao.insert(lorem.encrypt());
            lorem = dao.getByGender("male");
        }

        assertEquals("lorem", lorem.seed);
        assertEquals("lorem ipsum", lorem.name);
        assertEquals("male", lorem.gender);
        assertEquals("1976-12-03T00:00:00Z", lorem.dob);
        assertEquals("", lorem.decrypt().getEmail());

        dao.delete(lorem);
        lorem = dao.getByGender("male");
        assertNull(lorem);
    }

    /**
     * Given an empty users on local storage,
     * when inserted as new users,
     * then assert retrieved users are correct and deleted at the end.
     */
    @Test
    public void dao_getList() {
        List<User> lorem = dao.getList(0, 5);
        if (lorem.isEmpty()) {
            // only primary keys are unique
            User[] users = {
                    new User(1, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(2, "a", "b", "male", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(3, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(4, "a", "b", "male", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(5, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(6, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(7, "a", "b", "male", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(8, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(9, "a", "b", "male", "1970-01-01T00:00:00Z", "c@d.ef", 48),
                    new User(10, "a", "b", "female", "1970-01-01T00:00:00Z", "c@d.ef", 48),
            };
            for (User user : users) {
                dao.insert(user.encrypt());
            }
        }

        lorem = dao.getList(0, 5);
        assertEquals(5, lorem.size());

        lorem = dao.getList(7, 7);
        assertEquals(3, lorem.size());

        dao.deleteAll();
        lorem = dao.getList(0, 5);
        assertEquals(0, lorem.size());
    }
}
