package guru.qa.niffler.config;

import guru.qa.niffler.config.DockerConfig;
import guru.qa.niffler.config.LocalConfig;

import javax.annotation.Nonnull;

public interface Config {

  @Nonnull
  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? guru.qa.niffler.config.DockerConfig.INSTANCE
        : guru.qa.niffler.config.LocalConfig.INSTANCE;
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String spendUrl();

  @Nonnull
  String spendJdbcUrl();

  @Nonnull
  String currencyJdbcUrl();

  @Nonnull
  default String ghUrl() {
    return "https://api.github.com/";
  }
}
