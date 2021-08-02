package controller.network.protocol;

import org.junit.Assert;
import org.junit.Test;

public class PacketMenuOptionTest extends PacketTest<PacketMenuOption> {

    public PacketMenuOptionTest() {
        super(PacketMenuOption.class);
    }

    @Test
    public void emptySerializationTest() {
        this.before = new PacketMenuOption(randomContextId(), new String[0], randomInt(3));

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketMenuOption(randomContextId(), new String[]{randomString()}, randomInt(3));

        this.serialize();
        this.equals();
    }

    @Test
    public void multipleSerializationTest() {
        final String[] arguments = new String[randomInt(3)];

        for (int index = 0; index < arguments.length; index++) {
            arguments[index] = randomString();
        }

        this.before = new PacketMenuOption(randomContextId(), arguments, randomInt(3));

        this.serialize();
        this.equals();
    }

    @Test
    public void responseSerializationTest() {
        this.before = new PacketMenuOption(new PacketMenuOption(randomContextId(),
                new String[]{randomString()}, randomInt(3)), randomString(), randomBoolean());

        this.serialize();
        this.equals();

        // Vergleiche Fehlermeldung und Wahrheitswert der Antwort.
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }

        Assert.assertEquals(this.before.isSuccess(), this.after.isSuccess());
    }

    @Test
    public void responseCreationTest() {
        this.before = new PacketMenuOption(randomContextId(), new String[]{randomString()}, randomInt(3));
        this.after = new PacketMenuOption(this.before, randomString(), randomBoolean());

        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID, Argument und Option
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        Assert.assertArrayEquals(this.before.getArguments(), this.after.getArguments());
        Assert.assertEquals(this.before.getOption(), this.after.getOption());
    }
}
