package com.toba;

public interface Queries {
    String QUERY_STRING = "select * from EVENTS where id=?";
    String INSERT_STRING = "insert into EVENTS VALUES (?, ?, ?, ?, ?, ?, ?)";
    String UPDATE_STRING = "update EVENTS set started=?, stopped=?, duration=?, alert=? where id=?";
}