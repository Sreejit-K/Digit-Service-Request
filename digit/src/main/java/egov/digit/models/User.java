package egov.digit.models;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    private int id;
    private String uid;
    private String password;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String avatar;
    private String gender;
    private String phone_number;
    private String social_insurance_number;
    private String date_of_birth;
    private Employment employment;
    private Address address;
    private CreditCard credit_card;
    private Subscription subscription;
}