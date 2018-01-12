package com.daedafusion.events.providers;

import com.daedafusion.sf.Provider;

import java.util.Optional;
import java.util.Set;

/**
 * Created by mphilpot on 7/6/14.
 */
public interface EventHandlerProvider extends Provider
{
    // Optional for exchange level providers (rabbitmq)
    Optional<String> getExchange();

    // Topic or Routing
    String getTopic();

    void handle(byte[] bytes);
}
