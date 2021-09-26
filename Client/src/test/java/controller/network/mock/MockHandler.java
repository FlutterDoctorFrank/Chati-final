package controller.network.mock;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MockHandler extends Handler {

    private final List<LogRecord> records;

    public MockHandler(@NotNull final String identifier) {
        final Logger logger = Logger.getLogger(identifier);

        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(this);

        this.records = new ArrayList<>();
        this.setLevel(Level.ALL);
    }

    @Override
    public void publish(@NotNull final LogRecord record) {
        records.add(record);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

    public boolean logged(@NotNull final Level level, @NotNull final String contains) {
        for (final LogRecord record : records) {
            if (record.getLevel() == level && record.getMessage().contains(contains)) {
                return true;
            }
        }

        return false;
    }

    public boolean logged() {
        return !this.records.isEmpty();
    }

    public void reset() {
        this.records.clear();
    }
}
