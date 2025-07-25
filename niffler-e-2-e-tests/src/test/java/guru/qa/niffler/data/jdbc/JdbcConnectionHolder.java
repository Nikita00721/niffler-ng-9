package guru.qa.niffler.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcConnectionHolder implements AutoCloseable {

    private final DataSource dataSources;
    private final Map<Long, Connection> threadConnections = new ConcurrentHashMap<>();

    public JdbcConnectionHolder(DataSource dataSources) {
        this.dataSources = dataSources;
    }

    public Connection connection() {
        return threadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {
                    try {
                        return dataSources.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to get connection", e);
                    }
                }
        );
    }

    @Override
    public void close() throws Exception {
        Optional.ofNullable(threadConnections.remove(Thread.currentThread().threadId()))
                .ifPresent(connection -> {
                    try {
                        if (connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        // NOP
                    }
                });
    }

    public void closerAllConnections() {
        threadConnections.values().forEach(
                connection -> {
                    try {
                        if (connection != null && connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        // NOP
                    }
                }
        );
    }

}
