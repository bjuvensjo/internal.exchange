package com.github.bjuvensjo.internal.exchange

import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.spi.Registry
import spock.lang.Specification

class ExchangeProcessorEndpointTest extends Specification {
    CamelContext context
    def registry
    def template

    def setup() {
        context = Mock(CamelContext)
        template = Mock(ProducerTemplate)
        context.createProducerTemplate() >> template

        registry = Mock(Registry)
        context.getRegistry() >> registry
    }

    def 'create processor'() {
        when:
        registry.findByType(_) >> [].toSet()

        def endpoint = new ExchangeProcessorEndpoint(context, 'foo')
        endpoint.headers = 'h1, h2'
        endpoint.properties = 'p1, p2'
        endpoint.body = true
        endpoint.property = 'property'

        def processor = (ExchangeProcessor) endpoint.createProcessor()

        then:
        processor.endpointUri == 'foo'
        processor.headers == ['h1', 'h2']
        processor.properties == ['p1', 'p2']
        processor.body
        processor.property == 'property'
        processor.template == template

    }

    def 'create producer template from context'() {
        when:
        registry.findByType(_) >> [].toSet()

        def endpoint = new ExchangeProcessorEndpoint(context, 'foo')
        def producerTemplate = endpoint.getProducerTemplate()

        then:
        producerTemplate == template
    }

    def 'get producer template from registry'() {
        when:
        registry.findByType(_) >> [template].toSet()

        def endpoint = new ExchangeProcessorEndpoint(context, 'foo')
        def producerTemplate = endpoint.getProducerTemplate()

        then:
        producerTemplate == template
    }
}
