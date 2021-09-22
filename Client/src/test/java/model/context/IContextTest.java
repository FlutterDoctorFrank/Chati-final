package model.context;

import model.context.spatial.SpatialContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IContextTest {

    IContextView iSpatialContext;

    @Before
    public void setUp() throws Exception {
        Context spatialContext = new SpatialContext("TestContext", Context.getGlobal());
        iSpatialContext = spatialContext;
    }

    @After
    public void tearDown() throws Exception {
        iSpatialContext = null;
    }

    @Test
    public void getContextId() {
        Assert.assertEquals(iSpatialContext.getContextId().toString(), "Global.TestContext");
    }

    @Test
    public void getContextName() {
        Assert.assertEquals(iSpatialContext.getContextName().toString(), "TestContext");
    }
}