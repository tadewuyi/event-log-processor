package com.toba;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

import static com.toba.Queries.*;

public class DatabaseApiImpl implements DatabaseApi {

    private static final Logger logger = LogManager.getLogger();
    private final PreparedStatement preparedQueryStatement;
    private final PreparedStatement preparedInsertStatement;
    private final PreparedStatement preparedUpdateStatement;

    public DatabaseApiImpl(ConnectionWrapper connectionWrapper) throws SQLException {
        this.preparedQueryStatement = connectionWrapper.getConnection().prepareStatement(QUERY_STRING);
        this.preparedInsertStatement = connectionWrapper.getConnection().prepareStatement(INSERT_STRING);
        this.preparedUpdateStatement = connectionWrapper.getConnection().prepareStatement(UPDATE_STRING);
    }

    private Optional<ApplicationEvent> getEntry(String id) throws SQLException {
        preparedQueryStatement.setString(1, id);
        ResultSet rs = preparedQueryStatement.executeQuery();

        if (!rs.next()) {
            return Optional.empty();
        } else {
            boolean stateIsStarted = rs.getLong("started") != 0;
            boolean stateIsStopped = rs.getLong("stopped") != 0;
            boolean processingComplete = stateIsStarted && stateIsStopped;

            if(!processingComplete){
                return Optional.of(new ApplicationEvent(rs.getString("id"),
                        stateIsStarted ? "STARTED" : "FINISHED",
                        rs.getString("type"),
                        rs.getString("host"),
                        stateIsStarted ? rs.getLong("started") : rs.getLong("stopped")));
            } else {
                return Optional.of(new ApplicationEvent().setProcessed(true));
            }
        }
    }

    private void insertNewEntry(ApplicationEvent event) throws SQLException {
        preparedInsertStatement.setString(1, event.getId());

        if (event.stateIsStarted()) {
            preparedInsertStatement.setLong(2, event.getTimestamp());
            preparedInsertStatement.setNull(3, Types.BIGINT);
        } else {
            preparedInsertStatement.setNull(2, Types.BIGINT);
            preparedInsertStatement.setLong(3, event.getTimestamp());
        }

        preparedInsertStatement.setNull(4, Types.BIGINT);

        if (event.getType() != null) {
            preparedInsertStatement.setString(5, event.getType());
            preparedInsertStatement.setString(6, event.getHost());
        } else {
            preparedInsertStatement.setNull(5, Types.VARCHAR);
            preparedInsertStatement.setNull(6, Types.LONGVARCHAR);
        }

        preparedInsertStatement.setNull(7, Types.BOOLEAN);

        preparedInsertStatement.executeUpdate();
    }

    private void updateEntry(ApplicationEvent newEvent, ApplicationEvent storedEvent) throws SQLException {

        boolean newEventStateIsStarted = newEvent.stateIsStarted();

        long duration = 0;
        if (newEventStateIsStarted) {
            preparedUpdateStatement.setLong(1, newEvent.getTimestamp());
            preparedUpdateStatement.setLong(2, storedEvent.getTimestamp());
            duration = storedEvent.getTimestamp() - newEvent.getTimestamp();
        } else {
            preparedUpdateStatement.setLong(1, storedEvent.getTimestamp());
            preparedUpdateStatement.setLong(2, newEvent.getTimestamp());
            duration = newEvent.getTimestamp() - storedEvent.getTimestamp();
        }

        preparedUpdateStatement.setLong(3, duration);
        preparedUpdateStatement.setBoolean(4, duration > 4);
        preparedUpdateStatement.setString(5, newEvent.getId());

        preparedUpdateStatement.executeUpdate();
    }

    public void insertOrUpdate(ApplicationEvent newEvent) throws SQLException {
        Optional<ApplicationEvent> retrievedEvent = getEntry(newEvent.getId());

        if (!retrievedEvent.isPresent()) {
            logger.debug("No entry found in table for " + newEvent + ". Inserting into db...");
            insertNewEntry(newEvent);
            logger.info("insertion completed successfully");
        } else {
            if(!retrievedEvent.get().isProcessed()){
                logger.debug("Entry found in table for " + newEvent + ". Updating db...");
                updateEntry(newEvent, retrievedEvent.get());
                logger.info("Update completed successfully");
            } else {
                logger.debug("Fully processed entry found in table for " + newEvent + ". Skipping insertion...");
            }
        }
    }
}
