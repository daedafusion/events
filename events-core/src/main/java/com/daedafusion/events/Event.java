package com.daedafusion.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.log4j.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event<T>
{
    private static final Logger log = Logger.getLogger(Event.class);

    private Long timestamp;
    private String topic;
    private String origin;

    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, defaultImpl=Object.class)
    private T payload;

    private String user;

    public Event()
    {
        timestamp = System.currentTimeMillis();
    }

    public Event(String origin)
    {
        this();
        this.origin = origin;
    }

    public Event(String origin, String user)
    {
        this(origin);
        this.user = user;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin(String origin)
    {
        this.origin = origin;
    }

    public T getPayload()
    {
        return payload;
    }

    public void setPayload(T payload)
    {
        this.payload = payload;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

}
