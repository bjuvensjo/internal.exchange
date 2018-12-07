package com.github.bjuvensjo.internal.exchange

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultExchange

class ExchangeProcessor implements Processor {
    String endpointUri
    ProducerTemplate template
    List<String> headers
    List<String> properties
    boolean body
    String property

    void process(Exchange exchange) {
        Exchange internalExchange = send(exchange)

        if (property != null) {
            exchange.setProperty(property, internalExchange)
        } else {
            exchange.in.body = internalExchange
        }

    }

    Exchange send(Exchange exchange) {
        template.send(endpointUri, createExchange(exchange))
    }

    Exchange createExchange(Exchange exchange) {
        def internalExchange = new DefaultExchange(exchange.context, exchange.pattern)
        internalExchange.in.headers = reduce(exchange.in.headers, headers)
        internalExchange.properties = reduce(exchange.properties, properties)
        internalExchange.in.body = body ? exchange.in.body : null
        internalExchange
    }

    Map<String, Object> reduce(Map<String, Object> map, List<String> patterns) {
        if (!patterns) {
            return [:]
        }
        map.findAll { name, value ->
            hasMatch(name, patterns)
        }
    }

    boolean hasMatch(String name, List<String> patterns) {
        if (!patterns) {
            return false
        }
        patterns.any { p -> name.matches(p) }
    }
}
