package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

class ServiceTest {
    private final DataAccess DATAACCESS = new MemoryDataAccess();
    private final Service SERVICE = new Service(DATAACCESS);

    @BeforeEach
    void clear() throws ResponseException {
        SERVICE.clear();
    }

    @Test
    void positiveRegister() throws ResponseException, DataAccessException {
        RegisterRequest testRegister = new RegisterRequest("test_username", "test_password", "test_email");
        SERVICE.register(testRegister);

        // Get user

        // Get authData
    }
}
