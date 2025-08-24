package guru.qa.niffler.data.jdbc;

import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.jdbc.JdbcConnectionHolder;
import guru.qa.niffler.data.jdbc.JdbcConnectionHolders;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class Connections {
  private static final Map<String, guru.qa.niffler.data.jdbc.JdbcConnectionHolder> holders = new ConcurrentHashMap<>();

  private Connections() {
  }

  @Nonnull
  public static guru.qa.niffler.data.jdbc.JdbcConnectionHolder holder(String jdbcUrl) {
    return holders.computeIfAbsent(
        jdbcUrl,
        key -> new guru.qa.niffler.data.jdbc.JdbcConnectionHolder(
            DataSources.dataSource(jdbcUrl)
        )
    );
  }

  @Nonnull
  public static guru.qa.niffler.data.jdbc.JdbcConnectionHolders holders(String... jdbcUrl) {
    List<guru.qa.niffler.data.jdbc.JdbcConnectionHolder> result = new ArrayList<>();
    for (String url : jdbcUrl) {
      result.add(holder(url));
    }
    return new JdbcConnectionHolders(result);
  }

  public static void closeAllConnections() {
    holders.values().forEach(JdbcConnectionHolder::closeAllConnections);
  }
}
