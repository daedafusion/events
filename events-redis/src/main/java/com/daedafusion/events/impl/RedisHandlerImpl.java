package com.daedafusion.events.impl;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.events.EventHandler;
import com.daedafusion.events.providers.EventHandlerProvider;
import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.LifecycleListener;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RedisHandlerImpl extends AbstractService<EventHandlerProvider> implements EventHandler
{
    private static final Logger log = Logger.getLogger(RedisHandlerImpl.class);

    private JedisPool pool;
    private ExecutorService es;

    public RedisHandlerImpl()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                pool = new JedisPool(Configuration.getInstance().getString("redis.hostname", "localhost"));
                es = Executors.newCachedThreadPool();
            }

            @Override
            public void start()
            {
                getProviders().forEach(ehp -> {
                    es.submit(() -> pool.getResource().subscribe(new ProviderConsumer(ehp), ehp.getTopic()));
                });
            }

            @Override
            public void stop()
            {
                es.shutdown();
                pool.close();
            }
        });
    }

    @Override
    public Set<String> getTopics()
    {
        return getProviders().stream().map(EventHandlerProvider::getTopic).collect(Collectors.toSet());
    }

    @Override
    public Class getProviderInterface()
    {
        return EventHandlerProvider.class;
    }

    private class ProviderConsumer extends JedisPubSub
    {
        private EventHandlerProvider provider;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         */
        public ProviderConsumer(EventHandlerProvider provider)
        {
            this.provider = provider;
        }

        @Override
        public void onMessage(String channel, String message)
        {
            provider.handle(message.getBytes());
        }
    }
}
