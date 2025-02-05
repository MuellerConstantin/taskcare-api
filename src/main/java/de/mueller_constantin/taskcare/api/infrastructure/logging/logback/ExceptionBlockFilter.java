package de.mueller_constantin.taskcare.api.infrastructure.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ExceptionBlockFilter extends Filter<ILoggingEvent> {
    private Class<?> exceptionClass;

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

        if (exceptionClass != null && exceptionClass.isInstance(throwable)) {
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }

    public void setExceptionClassName(final String exceptionClassName) {
        try {
            exceptionClass = Class.forName(exceptionClassName);
        } catch (final ClassNotFoundException exc) {
            throw new IllegalArgumentException("Class is unavailable: " + exceptionClassName, exc);
        }
    }
}
