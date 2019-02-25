package com.toba;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class EventLogProcessor {

    private static final ObjectReader OBJECT_READER = new ObjectMapper().readerFor(ApplicationEvent.class);
    private static final Logger logger = LogManager.getLogger();

    private final DatabaseApi databaseApi;
    private final IterableDataSource<String> dataSource;


    public EventLogProcessor(IterableDataSource<String> dataSource, DatabaseApi databaseApi) {
        this.dataSource = dataSource;
        this.databaseApi = databaseApi;
    }


    public void run() throws IOException, SQLException {
        Optional<String> line;
        while ((line = dataSource.get()).isPresent()) {
            logger.debug("Current line being processed: "+line.get());
            ApplicationEvent newEvent = OBJECT_READER.readValue(line.get());
            databaseApi.insertOrUpdate(newEvent);
        }
    }

}
