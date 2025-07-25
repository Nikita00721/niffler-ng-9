package guru.qa.niffler.data.tpl;

import guru.qa.niffler.data.jdbc.Connections;
import guru.qa.niffler.data.jdbc.JdbcConnectionHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class JdbcTransactionTemplate {

    private final JdbcConnectionHolder holder;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T> action, int isolationLevel) throws Exception {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            T result = action.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException rollbackException) {
                    throw new RuntimeException("Rollback failed", rollbackException);
                }
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            if (closeAfterAction.get() && connection != null) {
                holder.close();
            }
        }
    }

    public <T> T execute(Supplier<T> action) throws Exception {
        return execute(action, TRANSACTION_READ_COMMITTED);
    }
}
