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

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SymbolsControllerTest {

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
        final var stringBuilder = new StringBuilder("\"");
        stringBuilder.append(testSymbol.value()).append("\"");
        assertEquals(stringBuilder.toString(), response.getBody().get().get("value").toString());
    }
}
