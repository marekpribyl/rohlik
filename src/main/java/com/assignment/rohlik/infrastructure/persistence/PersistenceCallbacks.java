package com.assignment.rohlik.infrastructure.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Configuration
public class PersistenceCallbacks {

    @Bean
    AfterConvertCallback<Object> afterConvertCallback() {
        return (entity, table) -> processCallback(entity, table, WithEntityCallback::afterConvertCallback);
    }

    @Bean
    BeforeConvertCallback<Object> beforeConvertCallback() {
        return (entity, table) -> processCallback(entity, table, WithEntityCallback::beforeConvertCallback);
    }

    private <T> Mono<T> processCallback(T entity, SqlIdentifier table,
                                        BiFunction<WithEntityCallback<T>, SqlIdentifier, Mono<T>> callbackFunction) {
        return (entity instanceof WithEntityCallback<?>)
                ? callbackFunction.apply((WithEntityCallback<T>) entity, table)
                : Mono.just(entity);
    }
}
