package dao;

import java.sql.SQLException;
import model.User;

public interface UserDAO {
    void registerUser(User user) throws SQLException;

    User getUserByEmail(String email) throws SQLException;

    boolean emailExists(String email) throws SQLException;

    boolean normalizedNameExists(String normalizedName) throws SQLException;

    User getUserById(int userId) throws SQLException;
}
