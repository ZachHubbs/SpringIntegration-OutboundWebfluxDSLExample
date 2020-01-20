package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Configuration
@Slf4j
public class OutboundConfiguration {


    private static final String ACCEPT = "accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-type";

    @Bean
    public IntegrationFlow enrichmentFlow(){
        return IntegrationFlows.from("inboundChannel")
                .transform(this::transformPayloadToResponse)
                .log(message -> message.getPayload())
                .enrich(e -> e.requestChannel("reactiveChannel").propertyExpression("vendorSkus", "payload"))
                .log(message -> message)
                .routeToRecipients(r -> r.recipient("serviceChannel"))
                .get();
    }





    @Bean
    public IntegrationFlow outboundFlow() {
        return IntegrationFlows.from("reactiveChannel")
                .enrichHeaders(e -> e.header("Authorization", "Basic dXNlcjpwYXNzd29yZA=="))
                .log(message -> message.getPayload())
                .handle(WebFlux.<MultiValueMap<String, String>>outboundGateway(m ->
                        UriComponentsBuilder.fromUriString("http://localhost:8081/rest/hello")
//                                .queryParams(m.getPayload())
                                .build()
                                .toUri())
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .routeToRecipients(r -> r.recipient("unmarshallingChannel"))
                .get();

}



    @Bean
    public IntegrationFlow unmarshallingFlow(){
        return IntegrationFlows.from("unmarshallingChannel").transform(this::unMarshallVendorSkuArray).log(message -> "Unmarshalled Value :"+ message.getPayload())
                .routeToRecipients(r -> r.recipient("enrichedChannel"))
                .get();
    }

    private Response transformPayloadToResponse(String vendorCode){
        return new Response(vendorCode);
    }

    private List<String> unMarshallVendorSkuArray(String vendSkus){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(vendSkus, new TypeReference<List<String>>() {
            });
        } catch (IOException e){
            log.error("Unable to Unmarshall array");
            throw new RuntimeException();
        }
    }

}
