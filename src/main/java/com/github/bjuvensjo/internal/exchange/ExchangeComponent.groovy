package com.github.bjuvensjo.internal.exchange

import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultComponent

class ExchangeComponent extends DefaultComponent {

    Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        new ExchangeProcessorEndpoint(this.getCamelContext(), remaining)
    }
}
