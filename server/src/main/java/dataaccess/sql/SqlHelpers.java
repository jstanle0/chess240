package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

import static java.sql.Types.NULL;

public class SqlHelpers {

    protected int executeUpdate(String statement, Object... params) throws SQLException {
        try (var conn = DatabaseManager.getConnection();
            var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
        ) {
            setParams(preparedStatement, params);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                if (resultSet != null && resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    protected <T> Collection<T> executeQuery(String query, Function<ResultSet, T> mapper, Object... params) throws SQLException {
        try (
                var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query);
        ) {
            setParams(preparedStatement, params);
            var rs = preparedStatement.executeQuery();
            if (rs == null) {
                throw new SQLException("failed to get db response");
            }
            var result = new HashSet<T>();
            while (rs.next()) {
                result.add(mapper.apply(rs));
            }
            rs.close();
            return result;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    private void setParams(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> statement.setString(i + 1, p);
                case Integer p -> statement.setInt(i + 1, p);
                case null -> statement.setNull(i + 1, NULL);
                default -> {}
            }
        }
    }
}
