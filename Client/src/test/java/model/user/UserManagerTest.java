package model.user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserManagerTest {

    UserManager userManager;

    @Before
    public void setUp() throws Exception {
        userManager = UserManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        userManager = null;
    }

    @Test
    public void setInternUser() {
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getInternUser() {
    }

    @Test
    public void setModelObserver() {
    }

    @Test
    public void getModelObserver() {
    }
}