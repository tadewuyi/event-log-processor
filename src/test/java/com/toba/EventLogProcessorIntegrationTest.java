package com.toba;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EventLogProcessorIntegrationTest {

    private static final String CREATE_TABLE_STATEMENT = "CREATE TABLE EVENTS(id LONGVARCHAR, started BIGINT, stopped BIGINT, duration BIGINT, type VARCHAR(15), host LONGVARCHAR, alert BOOLEAN, PRIMARY KEY (id));";
    private static final String QUERY_STATEMENT = "SELECT * FROM EVENTS WHERE id=?";
    private EventLogProcessor eventLogProcessor;
    private Connection connection;
    private PreparedStatement preparedQueryStatement;

    @Before
    public void setUp() throws Exception {
        String path = new File(getClass().getClassLoader().getResource("testInput.txt").getFile()).getAbsolutePath();
        IterableDataSource<String> dataSource = new FileBasedDataSource(path);
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:event");
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection);
        connection.createStatement().execute(CREATE_TABLE_STATEMENT);
        preparedQueryStatement = connection.prepareStatement(QUERY_STATEMENT);
        DatabaseApi databaseApi = new DatabaseApiImpl(connectionWrapper);
        eventLogProcessor = new EventLogProcessor(dataSource, databaseApi);
    }

    @Test
    public void run() throws Exception{
        eventLogProcessor.run();

        ArrayList<DatabaseResult> databaseResults = new ArrayList<>();

        databaseResults.add(new DatabaseResult("scsmbstgre", 1491377495212L, 1491377495217L, "APPLICATION_LOG", "12345", true));
        databaseResults.add(new DatabaseResult("scsmbstgrf", 1491377495213L, 1491377495216L, null, null, false));
        databaseResults.add(new DatabaseResult("scsmbstgrg", 1491377495210L, 1491377495218L, null, null, true));

        for (DatabaseResult databaseResult : databaseResults){
            preparedQueryStatement.setString(1, databaseResult.id);
            ResultSet resultSet = preparedQueryStatement.executeQuery();
            assertResultsAreAsExpected(databaseResult, resultSet);
        }
    }

    private void assertResultsAreAsExpected(DatabaseResult expectedResult, ResultSet resultSet) throws Exception{
        resultSet.next();
        Assert.assertEquals(expectedResult.id, resultSet.getString(1));
        Assert.assertEquals(expectedResult.started, resultSet.getLong(2));
        Assert.assertEquals(expectedResult.stopped, resultSet.getLong(3));
        Assert.assertEquals(expectedResult.duration, resultSet.getLong(4));
        Assert.assertEquals(expectedResult.type, resultSet.getString(5));
        Assert.assertEquals(expectedResult.host, resultSet.getString(6));
        Assert.assertEquals(expectedResult.alert, resultSet.getBoolean(7));
    }

    private class DatabaseResult{

        final String id;
        final long started;
        final long stopped;
        final long duration;
        final String type;
        final String host;
        final boolean alert;

        public DatabaseResult(String id, long started, long stopped, String type, String host, boolean alert) {
            this.id = id;
            this.started = started;
            this.stopped = stopped;
            this.duration = stopped - started;
            this.type = type;
            this.host = host;
            this.alert = alert;
        }
    }

}
