package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record SpendJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("spendDate")
    Date spendDate,
    @JsonProperty("category")
    guru.qa.niffler.model.CategoryJson category,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("amount")
    Double amount,
    @JsonProperty("description")
    String description,
    @JsonProperty("username")
    String username) {

    @Nonnull
    public static SpendJson fromEntity(SpendEntity entity) {
        final CategoryEntity category = entity.getCategory();
        final String username = entity.getUsername();

        return new SpendJson(
            entity.getId(),
            entity.getSpendDate(),
            new CategoryJson(
                category.getId(),
                category.getName(),
                username,
                category.isArchived()
            ),
            entity.getCurrency(),
            entity.getAmount(),
            entity.getDescription(),
            username
        );
    }
}
