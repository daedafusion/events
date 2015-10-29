package com.daedafusion.events.impl;

import com.daedafusion.events.Event;
import com.daedafusion.events.EventPublisher;
import com.daedafusion.events.providers.EventPublisherProvider;
import com.daedafusion.sf.AbstractService;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/5/14.
 */
public class EventPublisherImpl extends AbstractService<EventPublisherProvider> implements EventPublisher
{
    private static final Logger log = Logger.getLogger(EventPublisherImpl.class);

    @Override
    public void publish(Event event)
    {
        for(EventPublisherProvider epp : getProviders())
        {
            epp.publish(event);
        }
    }

    @Override
    public Class getProviderInterface()
    {
        return EventPublisherProvider.class;
    }
}
