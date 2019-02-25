package com.toba;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ApplicationEvent {

    private String id;
    private String state;
    private String type;
    private String host;
    private long timestamp;
    private boolean processed;

    public ApplicationEvent() {
    }

    public ApplicationEvent(String id, String state, String type, String host, long timestamp) {
        this.id = id;
        this.state = state;
        this.type = type;
        this.host = host;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isProcessed() {
        return processed;
    }

    public ApplicationEvent setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    public boolean stateIsStarted(){
        return "STARTED".equalsIgnoreCase(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationEvent event = (ApplicationEvent) o;

        return new EqualsBuilder()
                .append(timestamp, event.timestamp)
                .append(id, event.id)
                .append(state, event.state)
                .append(type, event.type)
                .append(host, event.host)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(state)
                .append(type)
                .append(host)
                .append(timestamp)
                .toHashCode();
    }
}
