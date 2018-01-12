package com.daedafusion.events.providers;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.events.Event;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPublisherProvider extends AbstractProvider implements EventPublisherProvider
{
    private static final Logger log = Logger.getLogger(RedisPublisherProvider.class);

    private JedisPool pool;

    public RedisPublisherProvider()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                pool = new JedisPool(new JedisPoolConfig(), Configuration.getInstance().getString("redis.hostname", "localhost"));
            }

            @Override
            public void stop()
            {
                pool.close();
            }
        });
    }

    @Override
    public void publish(Event event)
    {
        try(Jedis jedis = pool.getResource())
        {
            jedis.publish(event.getTopic(), event.getPayload());
        }
    }
}
