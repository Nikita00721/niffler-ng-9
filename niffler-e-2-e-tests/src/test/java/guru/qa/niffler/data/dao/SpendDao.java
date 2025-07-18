package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);
    Optional<SpendEntity> findSpendById(UUID uuid);
    List<SpendEntity> findAllByUsername(String username);
    void deleteSpend(SpendEntity spend);

    List<SpendEntity> findAll();
}
