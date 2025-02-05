package de.mueller_constantin.taskcare.api.infrastructure.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class ExceptionThrottlingFilter extends Filter<ILoggingEvent> {
    private Class<?> exceptionClass;
    private final AtomicInteger exceptionCounter = new AtomicInteger(0);
    private volatile long lastLoggedTime = 0;

    @Setter
    private long throttleIntervalMs = 60_000;

    @Setter
    private int maxSkipped = 10;

    @Setter
    private boolean enableCauseMatching = false;

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        IThrowableProxy throwableProxy = iLoggingEvent.getThrowableProxy();

        if (throwableProxy == null) {
            return FilterReply.NEUTRAL;
        }

        if (!(throwableProxy instanceof ThrowableProxy throwableProxyImpl)) {
            return FilterReply.NEUTRAL;
        }

        final Throwable throwable = throwableProxyImpl.getThrowable();

        boolean match = enableCauseMatching ?
                matchesExceptionOrCause(throwable, exceptionClass) :
                exceptionClass.isInstance(throwable);

        if (match) {
            int count = exceptionCounter.incrementAndGet();
            long now = System.currentTimeMillis();

            if (now - lastLoggedTime > throttleIntervalMs || count % maxSkipped == 0) {
                lastLoggedTime = now;
                exceptionCounter.set(0);
                return FilterReply.NEUTRAL;
            }

            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }

    private boolean matchesExceptionOrCause(Throwable throwable, Class<?> exceptionClass) {
        while (throwable != null) {
            if (exceptionClass.isInstance(throwable)) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

    public void setExceptionClassName(final String exceptionClassName) {
        try {
            exceptionClass = Class.forName(exceptionClassName);
        } catch (final ClassNotFoundException exc) {
            throw new IllegalArgumentException("Class is unavailable: " + exceptionClassName, exc);
        }
    }
}
