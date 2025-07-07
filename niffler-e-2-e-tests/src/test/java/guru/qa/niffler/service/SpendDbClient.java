package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.Databases.transaction;


public class SpendDbClient {
    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity =  new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
        }, CFG.spendJdbcUrl());
    }

    public SpendJson findSpend(UUID uuid) {
        return transaction(connection -> {
            SpendDao spendDao = new SpendDaoJdbc(connection);
            return spendDao.findSpendById(uuid)
                    .map(SpendJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("Spend not found with id: " + uuid));
        }, CFG.spendJdbcUrl());
    }

    public List<SpendJson> findSpendsByUsername(String username) {
        return transaction(connection -> {
            SpendDao spendDao = new SpendDaoJdbc(connection);
            List<SpendEntity> spends = spendDao.findAllByUsername(username);
            return spends.stream()
                    .map(SpendJson::fromEntity)
                    .collect(Collectors.toList());
        }, CFG.spendJdbcUrl());
    }
}
