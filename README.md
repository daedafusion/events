[![Build Status](https://travis-ci.org/daedafusion/events.svg?branch=master)](https://travis-ci.org/daedafusion/events)

# events

Pluggable Events Framework using [Service Framework](https://github.com/daedafusion/service-framework)

## Maven

```xml
<dependency>
    <groupId>com.daedafusion</groupId>
    <artifactId>events-core</artifactId>
    <version>1.0</version>
</dependency>
```

# Providers

`events-rabbitmq` is a basic provider for rabbitmq

## Framework Configuration

```xml
<dependency>
    <groupId>com.daedafusion</groupId>
    <artifactId>events-rabbitmq</artifactId>
    <version>1.0</version>
    <classifier>plugin</classifier>
    <type>zip</type>
</dependency>
```

    managedObjectDescriptions:
    - infClass: com.df.argos.commons.events.framework.EventPublisher
      implClass: com.df.argos.commons.events.framework.impl.EventPublisherImpl
    
    - infClass: com.df.argos.commons.events.framework.providers.EventPublisherProvider
      implClass: com.df.argos.commons.events.framework.providers.RabbitMQPublisherProvider
      loaderUri: framework://loader/events-rabbitmq/
      
    loaderDescriptions:
    - uri: framework://loader/events-rabbitmq/
      loaderClass: com.df.argos.commons.sf.loader.impl.ZipLoader
      resource: file:///opt/argos/plugins/events-rabbitmq-plugin.zip
      properties: {pluginName: events-rabbitmq}