package dev.kikugie.elytratrims.util;

import dev.kikugie.elytratrims.ElytraTrims;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class LogWrapper {
    private static final String TEMPLATE = "[%s]: %s";
    private final Logger logger;
    private final String executor;

    public LogWrapper(Logger logger, String executor) {
        this.logger = logger;
        this.executor = executor;
    }

    public static LogWrapper of(String executor) {
        return new LogWrapper(ElytraTrims.LOGGER, executor);
    }

    public static LogWrapper of(Class<?> executor) {
        return new LogWrapper(ElytraTrims.LOGGER, executor.getSimpleName());
    }

    public void info(String message, Object... args) {
        this.logger.info(TEMPLATE.formatted(this.executor, message), args);
    }

    public void warn(String message, Object... args) {
        this.logger.warn(TEMPLATE.formatted(this.executor, message), args);
    }

    public void error(String message, Object... args) {
        this.logger.error(TEMPLATE.formatted(this.executor, message), args);
    }

    public void debug(String message, Object... args) {
        this.logger.debug(TEMPLATE.formatted(this.executor, message), args);
    }
}
