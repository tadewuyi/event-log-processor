package com.toba.config;

import com.toba.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Config {

    private static final String CREATE_TABLE_STATEMENT = "SET DATABASE DEFAULT TABLE TYPE CACHED;\n"+
            "CREATE TABLE IF NOT EXISTS EVENTS(id LONGVARCHAR, started BIGINT, stopped BIGINT, duration BIGINT, type VARCHAR(15), host LONGVARCHAR, alert BOOLEAN, PRIMARY KEY (id));";


    @Bean
    public IterableDataSource<String> dataSource() {
        return new FileBasedDataSource("input.txt");
    }

    @Bean
    @Autowired
    public DatabaseApi databaseApi(ConnectionWrapper connectionWrapper) throws SQLException {
        return new DatabaseApiImpl(connectionWrapper);
    }

    @Bean
    public ConnectionWrapper connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:eventdb/eventdb;shutdown=true",
                "SA",
                "");
        connection.createStatement().execute(CREATE_TABLE_STATEMENT);
        return new ConnectionWrapper(connection);
    }

    @Bean
    @Autowired
    public EventLogProcessor eventLogProcessor(IterableDataSource<String> dataSource, DatabaseApi databaseApi) {
        return new EventLogProcessor(dataSource, databaseApi);
    }
}
