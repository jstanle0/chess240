package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class SqlHelpers {
    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) preparedStatement.setString(i + 1, p);
                else if (param instanceof Integer p) preparedStatement.setInt(i + 1, p);
                else if (param == null) preparedStatement.setNull(i + 1, NULL);
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
