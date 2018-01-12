package com.daedafusion.events.impl;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.events.Exchanges;
import com.daedafusion.events.EventHandler;
import com.daedafusion.events.providers.EventHandlerProvider;
import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.LifecycleListener;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mphilpot on 7/6/14.
 */
public class RabbitMQHandlerImpl extends AbstractService<EventHandlerProvider> implements EventHandler
{
    private static final Logger log = Logger.getLogger(RabbitMQHandlerImpl.class);

    private Connection connection;
    private Channel channel;

    private List<String> consumerTags;

    public RabbitMQHandlerImpl()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                consumerTags = new ArrayList<>();

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
                for(EventHandlerProvider ehp : getProviders())
                {
                    try
                    {
                        ProviderConsumer pc = new ProviderConsumer(channel, ehp);

                        channel.exchangeDeclare(ehp.getExchange().orElse("default"), "topic");
                        String queueName = channel.queueDeclare().getQueue();
                        channel.queueBind(queueName, ehp.getExchange().orElse("default"), ehp.getTopic());

                        String consumerTag = UUID.randomUUID().toString();

                        channel.basicConsume(queueName, false, consumerTag, pc);

                        consumerTags.add(consumerTag);
                    }
                    catch (IOException e)
                    {
                        log.warn("Unable to bind queue to exchange", e);
                    }
                }
            }

            @Override
            public void stop()
            {
                for(String consumerTag : consumerTags)
                {
                    try
                    {
                        channel.basicCancel(consumerTag);
                    }
                    catch (IOException e)
                    {
                        log.warn(String.format("Unable to cancel consumer %s", consumerTag), e);
                    }
                }
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
                    log.warn("Unable to close rabbitmq connection", e);
                }
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

    private class ProviderConsumer extends DefaultConsumer
    {
        private EventHandlerProvider provider;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public ProviderConsumer(Channel channel, EventHandlerProvider provider)
        {
            super(channel);
            this.provider = provider;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
        {
            provider.handle(body);

            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }


}
