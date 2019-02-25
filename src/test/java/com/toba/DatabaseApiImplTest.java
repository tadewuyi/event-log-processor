package com.toba;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import static com.toba.Queries.*;
import static org.mockito.Mockito.*;

public class DatabaseApiImplTest {
    DatabaseApiImpl databaseApi;
    private PreparedStatement preparedQueryStatement;
    private PreparedStatement preparedInsertStatement;
    private PreparedStatement preparedUpdateStatement;

    @Before
    public void setUp() throws Exception {
        Connection mockConnection = mock(Connection.class);
        preparedQueryStatement = mock(PreparedStatement.class);
        preparedInsertStatement = mock(PreparedStatement.class);
        preparedUpdateStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(QUERY_STRING)).thenReturn(preparedQueryStatement);
        when(mockConnection.prepareStatement(INSERT_STRING)).thenReturn(preparedInsertStatement);
        when(mockConnection.prepareStatement(UPDATE_STRING)).thenReturn(preparedUpdateStatement);
        databaseApi = new DatabaseApiImpl(new ConnectionWrapper(mockConnection));
    }

    @Test
    public void testThatDatabaseIsUpdatedWithNewEntryForStartedEvent() throws Exception {
        final ApplicationEvent event = new ApplicationEvent("123", "STARTED", null, null, 12345);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        when(preparedQueryStatement.executeQuery()).thenReturn(resultSet);

        databaseApi.insertOrUpdate(event);
        verify(preparedQueryStatement, times(1)).setString(1, event.getId());
        verify(preparedInsertStatement, times(1)).setLong(2, event.getTimestamp());
        verify(preparedInsertStatement, times(1)).setNull(3, Types.BIGINT);
        verify(preparedInsertStatement, times(1)).setNull(5, Types.VARCHAR);
        verify(preparedInsertStatement, times(1)).setNull(6, Types.LONGVARCHAR);
        verify(preparedInsertStatement, times(1)).setNull(7, Types.BOOLEAN);
        verify(preparedInsertStatement, times(1)).executeUpdate();
    }

    @Test
    public void testThatDatabaseIsUpdatedWithNewEntryForFinishedEvent() throws Exception {
        final ApplicationEvent event = new ApplicationEvent("123", "FINISHED", null, null, 12345);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        when(preparedQueryStatement.executeQuery()).thenReturn(resultSet);

        databaseApi.insertOrUpdate(event);
        verify(preparedQueryStatement, times(1)).setString(1, event.getId());
        verify(preparedInsertStatement, times(1)).setNull(2, Types.BIGINT);
        verify(preparedInsertStatement, times(1)).setLong(3, event.getTimestamp());
        verify(preparedInsertStatement, times(1)).setNull(5, Types.VARCHAR);
        verify(preparedInsertStatement, times(1)).setNull(6, Types.LONGVARCHAR);
        verify(preparedInsertStatement, times(1)).setNull(7, Types.BOOLEAN);
        verify(preparedInsertStatement, times(1)).executeUpdate();
    }

    @Test
    public void testThatDatabaseIsUpdatedWithNewEntryForEventWithTypeAndHost() throws Exception {
        final ApplicationEvent event = new ApplicationEvent("123", "FINISHED", "APPLICATION_LOG", "12345", 12345);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        when(preparedQueryStatement.executeQuery()).thenReturn(resultSet);

        databaseApi.insertOrUpdate(event);
        verify(preparedQueryStatement, times(1)).setString(1, event.getId());
        verify(preparedInsertStatement, times(1)).setNull(2, Types.BIGINT);
        verify(preparedInsertStatement, times(1)).setLong(3, event.getTimestamp());
        verify(preparedInsertStatement, times(1)).setString(5, event.getType());
        verify(preparedInsertStatement, times(1)).setString(6, event.getHost());
        verify(preparedInsertStatement, times(1)).setNull(7, Types.BOOLEAN);
        verify(preparedInsertStatement, times(1)).executeUpdate();
    }

    @Test
    public void testThatDatabaseIsUpdatedForStartedEvent() throws Exception {
        final ApplicationEvent event = new ApplicationEvent("123", "STARTED", null, null, 12345);
        final ApplicationEvent storedEvent = new ApplicationEvent("123", "FINISHED", null, null, 12349);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("started")).thenReturn(0L);
        when(resultSet.getLong("stopped")).thenReturn(storedEvent.getTimestamp());
        when(resultSet.getString("id")).thenReturn(storedEvent.getId());
        when(resultSet.getString("type")).thenReturn(storedEvent.getType());
        when(resultSet.getString("host")).thenReturn(storedEvent.getHost());

        when(preparedQueryStatement.executeQuery()).thenReturn(resultSet);

        databaseApi.insertOrUpdate(event);
        verify(preparedQueryStatement, times(1)).setString(1, event.getId());
        verify(preparedUpdateStatement, times(1)).setLong(1, event.getTimestamp());

        verify(preparedUpdateStatement, times(1)).setLong(2, storedEvent.getTimestamp());
        verify(preparedUpdateStatement, times(1)).setLong(3, storedEvent.getTimestamp()-event.getTimestamp());
        verify(preparedUpdateStatement, times(1)).setBoolean(4, storedEvent.getTimestamp()-event.getTimestamp() > 4);
        verify(preparedUpdateStatement, times(1)).setString(5, event.getId());
        verify(preparedUpdateStatement, times(1)).executeUpdate();
    }

    @Test
    public void testThatDatabaseIsUpdatedForFinishedEvent() throws Exception {
        final ApplicationEvent event = new ApplicationEvent("123", "FINISHED", null, null, 12349);
        final ApplicationEvent storedEvent = new ApplicationEvent("123", "STARTED", null, null, 12345);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("started")).thenReturn(storedEvent.getTimestamp());
        when(resultSet.getString("id")).thenReturn(storedEvent.getId());
        when(resultSet.getString("type")).thenReturn(storedEvent.getType());
        when(resultSet.getString("host")).thenReturn(storedEvent.getHost());

        when(preparedQueryStatement.executeQuery()).thenReturn(resultSet);

        databaseApi.insertOrUpdate(event);
        verify(preparedQueryStatement, times(1)).setString(1, event.getId());
        verify(preparedUpdateStatement, times(1)).setLong(1, storedEvent.getTimestamp());

        verify(preparedUpdateStatement, times(1)).setLong(2, event.getTimestamp());
        verify(preparedUpdateStatement, times(1)).setLong(3, event.getTimestamp()-storedEvent.getTimestamp());
        verify(preparedUpdateStatement, times(1)).setBoolean(4, event.getTimestamp()-storedEvent.getTimestamp() > 4);
        verify(preparedUpdateStatement, times(1)).setString(5, event.getId());
        verify(preparedUpdateStatement, times(1)).executeUpdate();
    }

    private ApplicationEvent event(){
        return new ApplicationEvent("123", "STARTED", "dw", "dw", 12345);
    }

}