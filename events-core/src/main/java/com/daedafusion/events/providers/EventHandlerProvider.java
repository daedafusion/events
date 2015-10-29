package com.daedafusion.events.providers;

import com.daedafusion.sf.Provider;

import java.util.Set;

/**
 * Created by mphilpot on 7/6/14.
 */
public interface EventHandlerProvider extends Provider
{
    String getExchange();
    Set<String> getRouting();

    void handle(byte[] bytes);
}
