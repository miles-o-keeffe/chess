package database;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import service.Service;

public class MySQLDataAccessTest {
    private static DataAccess dataAccess;

    @BeforeAll
    static void createDatabase() throws ResponseException, DataAccessException {
        dataAccess = new MySqlDataAccess();
    }

}
