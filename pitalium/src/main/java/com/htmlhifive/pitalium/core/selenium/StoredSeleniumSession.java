package com.htmlhifive.pitalium.core.selenium;

import java.util.Map;

public class StoredSeleniumSession {
    private final String sessionId;
    private final Map<String,Object> rawCapabilities;
    private final String dialect;

    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Object> getRawCapabilities() {
        return rawCapabilities;
    }

    public String getDialect() {
        return dialect;
    }

    public StoredSeleniumSession(String sessionId, Map<String, Object> rawCapabilities, String dialect) {
        this.sessionId = sessionId;
        this.rawCapabilities = rawCapabilities;
        this.dialect = dialect;
    }
}
