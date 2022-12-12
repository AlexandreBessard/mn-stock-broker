package com.broker.data;

import com.broker.broker.Symbol;
import com.github.javafaker.Faker;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Singleton
public class InMemoryStore {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryStore.class);
    private final Map<String, Symbol> symbol = new HashMap<>();
    private final Faker faker = new Faker();

    @PostConstruct
    public void initialize() {
        initializeWith(10);
    }

    public void initializeWith(int numberEntries) {
        symbol.clear();
        IntStream.range(0, numberEntries).forEach(i -> addNewSymbol());
    }

    private void addNewSymbol() {
        var symbol = new Symbol(faker.stock().nsdqSymbol());
        this.symbol.put(symbol.value(), symbol);
        LOG.debug("Added Symbol {}", symbol);
    }

    public Map<String, Symbol> getSymbol() {
        return symbol;
    }
}
