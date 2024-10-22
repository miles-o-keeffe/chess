package service;

import dataAccess.DataAccessException;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

class ServiceTest {
    static final Service service = new Service();

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
    }

    @Test
    void positiveRegister() throws ResponseException, DataAccessException {
        RegisterRequest testRegister = new RegisterRequest("test_username", "test_password", "test_email");
        service.register(testRegister);

        // Get user

        // Get authData
    }
}
