package com.dynacore.livemap;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.dialect.BindMarkersFactory;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.sql.render.SelectRenderContext;

import java.util.Optional;

public class CustomDialectConvertors {

    @RequiredArgsConstructor
    static class ExternalConnectionFactory implements ConnectionFactory {

        private final String name;

        @Override
        public Publisher<? extends Connection> create() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ConnectionFactoryMetadata getMetadata() {
            return () -> this.name;
        }
    }

    static class ExternalDialectProvider implements DialectResolver.R2dbcDialectProvider {

        @Override
        public Optional<R2dbcDialect> getDialect(ConnectionFactory connectionFactory) {

            if (connectionFactory.getMetadata().getName().equals("external")) {
                return Optional.of(ExternalDialect.INSTANCE);
            }
            return Optional.empty();
        }
    }


    enum ExternalDialect implements R2dbcDialect {

        INSTANCE;

        @Override
        public BindMarkersFactory getBindMarkersFactory() {
            return null;
        }

        @Override
        public LimitClause limit() {
            return null;
        }

        @Override
        public SelectRenderContext getSelectContext() {
            return null;
        }
    }
}
