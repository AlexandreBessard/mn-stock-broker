package com.broker;

import com.broker.broker.Symbol;
import com.broker.data.InMemoryStore;
import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SymbolsControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SymbolsControllerTest.class);

    @Inject
    @Client("/symbols")
    HttpClient httpClient;

    @Inject
    InMemoryStore inMemoryStore;

    @BeforeEach
    void setup() {
        inMemoryStore.initializeWith(10);
    }

    @Test
    void symbolsEndpoint() {
        //Async http client -> toBlocking()
        var response = httpClient.toBlocking().exchange("/", JsonNode.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(10, response.getBody().get().size());
    }

    @Test
    void symbolsEndpointReturnsCorrectSymbol() {
        var testSymbol = new Symbol("TEST");
        inMemoryStore.getSymbol().put(testSymbol.value(), testSymbol);
        //Async http client -> toBlocking()
        var response = httpClient.toBlocking().exchange("/" + testSymbol.value(), JsonNode.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        final var strBuilder = new StringBuilder("\"");
        strBuilder.append(testSymbol.value()).append("\"");
        assertEquals(strBuilder.toString(), response.getBody().get().get("value").toString());
    }

    @Test
    void symbolsEndpointQueryParameters() {
        var response = httpClient.toBlocking().exchange("/filter?max=10", JsonNode.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Max: 10: {}", response.getBody().get().toPrettyString());
        assertEquals(10, response.getBody().get().size());

        response = httpClient.toBlocking().exchange("/filter?offset=7", JsonNode.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Offset: 7 {}", response.getBody().get().toPrettyString());
        assertEquals(3, response.getBody().get().size());

        response = httpClient.toBlocking().exchange("/filter?max=2&offset=7", JsonNode.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Max 2, Offset: 7 {}", response.getBody().get().toPrettyString());
        assertEquals(2, response.getBody().get().size());
    }
}
