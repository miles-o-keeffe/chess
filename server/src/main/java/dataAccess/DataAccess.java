package dataAccess;

import model.UserData;

public interface DataAccess {
    UserData getUser(String userName);
}
