package com.github.bjuvensjo.internal.exchange

import org.apache.camel.*
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Specification
import spock.lang.Unroll

class ExchangeProcessorTest extends Specification {
    CamelContext context
    ProducerTemplate template

    def setup() {
        context = new DefaultCamelContext()
        context.start()
        template = Mock(ProducerTemplate)
    }

    def cleanup() {
        context.stop()
    }

    def 'exchange empty as body'() {
        when:
        def exchangeProcessor = new ExchangeProcessor(
                endpointUri: 'direct:foo/bar',
                template: template)
        def exchange = createExchangeWithBody('request')
        template.send(_ as String, _ as Exchange) >> { s, e -> e }
        exchangeProcessor.process(exchange)
        def internalExchange = exchange.in.getBody(Exchange)

        then:
        internalExchange.properties == [:]
        internalExchange.in.headers == [:]
        internalExchange.in.body == null
        exchange.in.body == internalExchange
    }

    def 'exchange empty as property'() {
        when:
        def exchangeProcessor = new ExchangeProcessor(
                endpointUri: 'direct:foo/bar',
                template: template,
                property: 'property')
        def exchange = createExchangeWithBody('request')
        template.send(_ as String, _ as Exchange) >> { s, e -> e }
        exchangeProcessor.process(exchange)
        def internalExchange = exchange.properties['property']

        then:
        internalExchange.properties == [:]
        internalExchange.in.headers == [:]
        internalExchange.in.body == null
        exchange.in.body == 'request'
    }

    def 'exchange with properties, headers and body'() {
        when:
        def exchangeProcessor = new ExchangeProcessor(
                endpointUri: 'direct:foo/bar',
                template: template,
                properties: ['.*'],
                headers: ['header1', 'header2'],
                body: true)
        def exchange = createExchangeWithBody('request')
        template.send(_ as String, _ as Exchange) >> { s, e -> e }
        exchangeProcessor.process(exchange)
        def internalExchange = exchange.in.getBody(Exchange)

        then:
        internalExchange.properties == [property1: '1', property2: '2']
        internalExchange.in.headers == [header1: '1', header2: '2']
        internalExchange.in.body == 'request'
        exchange.in.body == internalExchange
    }

    def createExchangeWithBody(Object body, ExchangePattern exchangePattern = ExchangePattern.InOut) {
        Exchange exchange = new DefaultExchange(context, exchangePattern)
        exchange.setProperty('property1', '1')
        exchange.setProperty('property2', '2')
        Message message = exchange.getIn()
        message.setHeader('testClass', this.getClass().getName())
        message.setHeader('header1', '1')
        message.setHeader('header2', '2')
        message.setBody(body)
        exchange
    }

    @Unroll("call #name, #patterns, #expected")
    def 'has match'() {
        when:
        def exchangeProcessor = new ExchangeProcessor()

        then:
        expected == exchangeProcessor.hasMatch(name, patterns)

        where:
        name  | patterns | expected
        'foo' | null     | false
        'foo' | []       | false
        'foo' | ['bar']  | false
        'foo' | ['foo']  | true
        'foo' | ['.*']   | true
    }
}
