package com.toba;

import java.sql.SQLException;

public interface DatabaseApi {

    void insertOrUpdate(ApplicationEvent newEvent) throws SQLException;

}
