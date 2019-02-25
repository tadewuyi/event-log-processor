package com.toba;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper {
    private final Connection connection;

    public ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    //called when this bean is destroyed
    public void close() throws SQLException {
        connection.close();
    }
}
