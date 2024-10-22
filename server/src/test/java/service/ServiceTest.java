package service;

import dataaccess.DataAccessException;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

class ServiceTest {
    static final Service SERVICE = new Service();

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
