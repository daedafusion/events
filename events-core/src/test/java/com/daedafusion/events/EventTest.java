package com.daedafusion.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by mphilpot on 7/6/14.
 */
public class EventTest
{
    private static Logger log = Logger.getLogger(EventTest.class);

    @Test
    public void testSerialization() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        Event<String> stringEvent = new Event<>("test");
        stringEvent.setPayload("emergency");

        byte[] bytes = mapper.writeValueAsBytes(stringEvent);

        Event<String> result = mapper.readValue(bytes, new TypeReference<Event<String>>(){});

        assertThat(result.getPayload(), is("emergency"));
        assertThat(result.getTimestamp(), is(notNullValue()));
        assertThat(result.getUser(), is(nullValue()));

        Event<Map<String, String>> mapEvent = new Event<>("test", "jsmith");
        Map<String, String> payload = new HashMap<>();
        payload.put("someKey", "someValue");
        mapEvent.setPayload(payload);

        bytes = mapper.writeValueAsBytes(mapEvent);

        Event<Map<String, String>> mapResult = mapper.readValue(bytes, new TypeReference<Event<Map<String, String>>>(){});

        assertTrue(mapResult.getPayload().containsKey("someKey"));
        assertThat(mapResult.getUser(), is("jsmith"));
    }
}
