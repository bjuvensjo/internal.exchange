package com.github.bjuvensjo.internal.exchange

import org.apache.camel.CamelContext
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.ProcessorEndpoint
import org.apache.camel.spi.UriParam

class ExchangeProcessorEndpoint extends ProcessorEndpoint {
    @UriParam
    String headers
    @UriParam
    String properties
    @UriParam
    boolean body
    @UriParam
    String property

    ExchangeProcessorEndpoint(CamelContext camelContext, String endpointUri) {
        super(endpointUri, camelContext, null)
    }

    @Override
    Processor createProcessor() {
        new ExchangeProcessor(
                endpointUri: this.endpointUri,
                template: getProducerTemplate(),
                properties: properties ? properties.split(/ *, */).toList() : null,
                headers: headers ? headers.split(/ *, */).toList() : null,
                body: body,
                property: property)
    }

    ProducerTemplate getProducerTemplate() {
        Set<ProducerTemplate> producerTemplates = camelContext.getRegistry().findByType(ProducerTemplate.class)

        if (!producerTemplates.isEmpty()) {
            return producerTemplates.iterator().next()
        }
        this.getCamelContext().createProducerTemplate()
    }
}
