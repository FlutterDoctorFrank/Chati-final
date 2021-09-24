package model.context;

import model.ContextParameters;
import model.RandomValues;
import model.context.spatial.SpatialContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IContextTest {

    IContextView iSpatialContext;
    String contextName;

    @Before
    public void setUp() {
        contextName = RandomValues.random8LengthString();
        iSpatialContext = new SpatialContext(contextName, Context.getGlobal());
    }

    @After
    public void tearDown() {
        iSpatialContext = null;
    }

    @Test
    public void getContextId() {
        Assert.assertEquals(iSpatialContext.getContextId().toString(), ContextParameters.GLOBAL_NAME + "." + contextName);
    }

    @Test
    public void getContextName() {
        Assert.assertEquals(iSpatialContext.getContextName(), contextName);
    }
}