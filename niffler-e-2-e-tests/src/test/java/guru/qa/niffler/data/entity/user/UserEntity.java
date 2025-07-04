package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String fullName;
    private String firstName;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson json) {
        UserEntity user = new UserEntity();
        user.setId(json.id());
        user.setUsername(json.username());
        user.setCurrency(json.currency());
        user.setFullName(json.fullname());
        user.setFirstName(json.firstname());
        user.setSurname(json.surname());
        if (json.photo() != null) {
            user.setPhoto(json.photo().getBytes());
        }
        if (json.photoSmall() != null) {
            user.setPhotoSmall(json.photoSmall().getBytes());
        }
        return user;
    }
}
