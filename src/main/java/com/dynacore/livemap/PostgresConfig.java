package com.dynacore.livemap;


import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;


@Profile("traveltime")
@Configuration
@EnableR2dbcRepositories
public class PostgresConfig extends AbstractR2dbcConfiguration {

   @Bean("postgresStandardFactory")
    public PostgresqlConnectionFactory connectionFactory2() {

       PostgresqlConnectionConfiguration configuration =  PostgresqlConnectionConfiguration.builder()
               .host("localhost")
               .port(5432)
               .username("postgres")
               .password("admin")
               .database("trafficdata")
               .build();

        return new PostgresqlConnectionFactory(configuration);
    }

    @Bean("postgresPoolFactory")
    public ConnectionFactory connectionFactory() {

        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql") // driver identifier, PROTOCOL is delegated as DRIVER by the pool.
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "postgres")
                .option(PASSWORD, "admin")
                .option(DATABASE, "trafficdata")
                .build());

    }

//    @Bean
//    @Override
//    public R2dbcCustomConversions r2dbcCustomConversions() {
//
//        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
//        converterList.add(new org.springframework.data.r2dbc.test.PersonReadConverter());
//        converterList.add(new org.springframework.data.r2dbc.test.PersonWriteConverter());
//        return new R2dbcCustomConversions(getStoreConversions(), converterList);
//    }


//    @ReadingConverter
//    public class PersonReadConverter implements Converter<Row, Person> {
//
//        public Person convert(Row source) {
//            Person p = new Person(source.get("id", String.class),source.get("name", String.class));
//            p.setAge(source.get("age", Integer.class));
//            return p;
//        }
//    }

//    @WritingConverter
//    public class PersonWriteConverter implements Converter<Person, OutboundRow> {
//
//        public OutboundRow convert(Person source) {
//            OutboundRow row = new OutboundRow();
//            row.put("id", SettableValue.from(source.getId()));
//            row.put("name", SettableValue.from(source.getFirstName()));
//            row.put("age", SettableValue.from(source.getAge()));
//            return row;
//        }
//    }
}
