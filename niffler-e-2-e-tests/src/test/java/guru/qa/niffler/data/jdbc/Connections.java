package guru.qa.niffler.data.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Connections {
    public Connections() {}

    private static final Map<String, JdbcConnectionHolder> holders = new java.util.concurrent.ConcurrentHashMap<>();

    public static JdbcConnectionHolder holder(String jdbcUrl) {
        return holders.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(DataSources.dataSource(jdbcUrl))
        );
    }

    public static JdbcConnectionHolders holders(String... jdbcUrl) {
        List<JdbcConnectionHolder> result = new ArrayList<>();
        for (String url : jdbcUrl) {
            result.add(holder(url));
        }
        return new JdbcConnectionHolders(result);
    }

    public static void closerAllConnections() {
        holders.values().forEach(JdbcConnectionHolder::closerAllConnections);
    }
}
