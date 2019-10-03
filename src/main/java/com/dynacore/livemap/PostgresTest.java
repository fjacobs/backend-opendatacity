package com.dynacore.livemap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.r2dbc.query.Criteria.where;

public class PostgresTest {

    private DatabaseClient databaseClient;

    @Autowired
    public void PostgresTest(DatabaseClient client) {
        this.databaseClient = client;
        Mono<Void> completion = client.execute("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), age INTEGER);")
                .then();
    }


//    15.2. Using ConnectionFactoryUtils
//
//    The ConnectionFactoryUtils class is a convenient and powerful helper class that provides static methods to obtain connections
//    from ConnectionFactory and close connections (if necessary). It supports subscriber Context-bound connections with,
//    for example ConnectionFactoryTransactionManager.

    public void testUtils() {

    }





    public void affectedRows() {
        Mono<Integer> affectedRows = databaseClient.execute("UPDATE person SET name = 'Joe'")
                .fetch().rowsUpdated();
    }

    public Flux<Person> getAll() {
        return databaseClient.execute("SELECT id, name FROM mytable")
                .as(Person.class)
                .fetch().all();
    }


    //Mapping Results
//    You can customize result extraction beyond Map and POJO result extraction by providing an extractor BiFunction<Row, RowMetadata, T>.
//    The extractor function interacts directly with R2DBC’s Row and RowMetadata objects and can return arbitrary values (singular values, collections and maps, and objects).
    public void customExtractor() {
        Flux<String> names = databaseClient.execute("SELECT name FROM person")
            .map((row, rowMetadata) -> row.get("id", String.class))
            .all();
    }

//    Parameter binding supports two binding strategies:
//    By Index, using zero-based parameter indexes.
//    By Name, using the placeholder name.

    public void parameterBinding() {
        databaseClient.execute("INSERT INTO person (id, name, age) VALUES(:id, :name, :age)")
                .bind("id", "joe")
                .bind("name", "Joe")
                .bind("age", 34);
    }

    //-------
    public Flux<Person> getAll2() {
        return databaseClient.select()
                .from(Person.class)
                .fetch()
                .all();
    }


//    Methods for the Criteria Class
//
//The Criteria class provides the following methods, all of which correspond to SQL operators:
//Criteria and (String column): Adds a chained Criteria with the specified property to the current Criteria and returns the newly created one.
//Criteria or (String column): Adds a chained Criteria with the specified property to the current Criteria and returns the newly created one.
//Criteria greaterThan (Object o): Creates a criterion by using the > operator.
//Criteria greaterThanOrEquals (Object o): Creates a criterion by using the >= operator.
//Criteria in (Object…​ o): Creates a criterion by using the IN operator for a varargs argument.
//Criteria in (Collection<?> collection): Creates a criterion by using the IN operator using a collection.
//Criteria is (Object o): Creates a criterion by using column matching (property = value).
//Criteria isNull (): Creates a criterion by using the IS NULL operator.
//Criteria isNotNull (): Creates a criterion by using the IS NOT NULL operator.
//Criteria lessThan (Object o): Creates a criterion by using the < operator.
//Criteria lessThanOrEquals (Object o): Creates a criterion by using the ⇐ operator.
//Criteria like (Object o): Creates a criterion by using the LIKE operator without escape character processing.
//Criteria not (Object o): Creates a criterion by using the != operator.
//Criteria notIn (Object…​ o): Creates a criterion by using the NOT IN operator for a varargs argument.
//Criteria notIn (Collection<?> collection): Creates a criterion by using the NOT IN operator using a collection.

    //fluent API style :
    public Mono<Person> complexQuering() {
        return databaseClient.select()
                .from("legoset")
                .matching(where("firstname").is("John")
                        .and("lastname").in("Doe", "White"))
                .orderBy(desc("id"))
                .as(Person.class)
                .fetch()
                .one();
    }

    public void insert() {
           databaseClient.insert()
                .into(Person.class)
                .using(new Person("joe", "joe", 34))
                .then();
    }


}
