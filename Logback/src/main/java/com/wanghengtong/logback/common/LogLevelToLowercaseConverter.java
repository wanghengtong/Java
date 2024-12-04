package com.wanghengtong.logback.common;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogLevelToLowercaseConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return event.getLevel().levelStr.toLowerCase();
    }

}
