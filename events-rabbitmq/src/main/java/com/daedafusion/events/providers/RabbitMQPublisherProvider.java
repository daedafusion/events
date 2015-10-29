package com.daedafusion.events.providers;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.events.Event;
import com.daedafusion.events.Exchanges;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 7/5/14.
 */
public class RabbitMQPublisherProvider extends AbstractProvider implements EventPublisherProvider
{
    private static final Logger log = Logger.getLogger(RabbitMQPublisherProvider.class);

    private Connection connection;
    private Channel channel;

    private ObjectMapper mapper;

    public RabbitMQPublisherProvider()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                mapper = new ObjectMapper();

                String hostname = Configuration.getInstance().getString("rabbit.hostname", "localhost");

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(hostname);

                try
                {
                    connection = factory.newConnection();
                    channel = connection.createChannel();

                    channel.exchangeDeclare(Exchanges.SYSTEM_EXCHANGE, "topic", false);
                    channel.exchangeDeclare(Exchanges.USER_EXCHANGE, "topic", false);
                }
                catch (IOException e)
                {
                    log.warn("Could not connect rabbitmq", e);
                }

            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {

            }

            @Override
            public void teardown()
            {
                try
                {
                    channel.close();
                    connection.close();
                }
                catch (IOException e)
                {
                    log.warn("Unable to close connection to rabbitmq", e);
                }

            }
        });
    }

    @Override
    public void publish(Event event)
    {
        try
        {
            if (event.getUser() != null)
            {
                channel.basicPublish(Exchanges.USER_EXCHANGE, event.getTopic(), null, mapper.writeValueAsBytes(event));
            }
            else
            {
                channel.basicPublish(Exchanges.SYSTEM_EXCHANGE, event.getTopic(), null, mapper.writeValueAsBytes(event));
            }
        }
        catch (IOException e)
        {
            log.warn("Unable to publish event", e);
        }
    }
}
