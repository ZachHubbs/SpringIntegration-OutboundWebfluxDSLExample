package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.webflux.inbound.WebFluxInboundEndpoint;
import org.springframework.web.reactive.config.EnableWebFlux;
@Configuration
@EnableWebFlux
@EnableIntegration
public class ReactiveHttpConfiguration {


    @Bean
    public WebFluxInboundEndpoint simpleInboundEndpoint() {
        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression("#pathVariables.vendorId");
        WebFluxInboundEndpoint endpoint = new WebFluxInboundEndpoint();
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.setPathPatterns("/test/{vendorId}");
        requestMapping.setMethods(HttpMethod.POST);
        endpoint.setPayloadExpression(exp);
        endpoint.setRequestMapping(requestMapping);

        endpoint.setRequestChannelName("inboundChannel");
        return endpoint;
    }



    @ServiceActivator(inputChannel = "enrichedChannel")
    public String[] enriched ( String[] r) {
        return r;
    }

    @ServiceActivator(inputChannel = "serviceChannel")
        String service( Response r) {
        for(String sku: r.getVendorSkus()){
            System.out.println(sku);
        }

        return "It works!";
    }
}