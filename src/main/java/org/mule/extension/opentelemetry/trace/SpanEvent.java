package org.mule.extension.opentelemetry.trace;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.util.MultiMap;

public class SpanEvent {
    private String eventId;
    private String name;
    private MultiMap<String, String> attributes= MultiMap.emptyMultiMap();
    private ComponentLocation location ;
    public String getName() {
        return name;
    }

    public SpanEvent setName(String name) {
        this.name = name;
        return this;
    }

    public ComponentLocation getLocation() {
        return location;
    }

    public SpanEvent setLocation(ComponentLocation location) {
        this.location = location;
        return this;
    }

    public String getEventId() {
        return eventId;
    }

    public SpanEvent setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public MultiMap<String, String> getAttributes() {
        return attributes;
    }

    public SpanEvent setAttributes(MultiMap<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public String toString() {
        return "SpanEvent{" +
                "eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                ", attributes=" + attributes +
                ", location=" + location +
                '}';
    }
}
