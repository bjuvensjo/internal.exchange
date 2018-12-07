package com.github.bjuvensjo.internal.exchange

import org.apache.camel.*
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Specification
import spock.lang.Unroll

class ExchangeComponentTest extends Specification {
    CamelContext context
    ProducerTemplate template

    def setup() {
        context = new DefaultCamelContext()
        context.start()
        template = context.createProducerTemplate()
    }

    def cleanup() {
        context.stop()
    }

    def 'create endpoint'() {
        when:
        def component = new ExchangeComponent()
        component.camelContext = context
        Endpoint endpoint = component.createEndpoint('', 'remaining', [:])

        then:
        endpoint.class == ExchangeProcessorEndpoint.class
        endpoint.camelContext == context
        endpoint.endpointUri == 'remaining'
    }

    def 'component in register'() {
        expect:
        context.getComponent('iex').class == ExchangeComponent.class
    }

    @Unroll("Call #body, #expected")
    def 'options body'() {
        when:
        Exchange exchange = new DefaultExchange(context, ExchangePattern.InOut)
        exchange.in.body = 'request'
        template.send('iex:log?body=' + body, exchange)
        def internalExchange = exchange.in.body

        then:
        internalExchange.in.body == expected

        where:
        expected  | body
        'request' | true
        null      | false
    }

    def 'options headers'() {
        when:
        Exchange exchange = new DefaultExchange(context, ExchangePattern.InOut)
        exchange.in.body = 'request'
        exchange.in.headers = [header1: 'h1', header2: 'h2']
        template.send('iex:log?headers=header1,header.*', exchange)
        def internalExchange = exchange.in.body

        then:
        internalExchange.in.headers.containsKey('header1')
        internalExchange.in.headers.containsKey('header2')
    }

    def 'options properties'() {
        when:
        Exchange exchange = new DefaultExchange(context, ExchangePattern.InOut)
        exchange.in.body = 'request'
        exchange.properties = [property1: 'p1', property2: 'p2']
        template.send('iex:log?properties=property1,property.*', exchange)
        def internalExchange = exchange.in.body

        then:
        internalExchange.properties.containsKey('property1')
        internalExchange.properties.containsKey('property2')
    }

    def 'options property'() {
        when:
        Exchange exchange = new DefaultExchange(context, ExchangePattern.InOut)
        exchange.in.body = 'request'
        template.send('iex:log?body=true&property=foo', exchange)
        def internalExchange = exchange.properties['foo']

        then:
        internalExchange.in.body == 'request'
    }
}
