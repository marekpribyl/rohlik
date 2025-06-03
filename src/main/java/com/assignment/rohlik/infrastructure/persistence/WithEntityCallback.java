package com.assignment.rohlik.infrastructure.persistence;

import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Mono;

public interface WithEntityCallback<T> {

    Mono<T> afterConvertCallback(SqlIdentifier tableName);

    Mono<T> beforeConvertCallback(SqlIdentifier tableName);

}
