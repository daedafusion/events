package com.daedafusion.events;

import java.util.Optional;

public interface Event {
    String getTopic();
    String getPayload();
    Optional<String> getUser();
}
