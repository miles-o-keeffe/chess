package service;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceTest {
    static final Service service = new Service();

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
    }

    @Test
    void positiveRegister() throws ResponseException {

    }
}
