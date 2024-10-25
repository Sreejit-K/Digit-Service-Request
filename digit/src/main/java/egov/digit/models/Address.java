package egov.digit.models;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class Address {
    private String city;
    private String street_name;
    private String street_address;
    private String zip_code;
    private String state;
    private String country;
    private Coordinates coordinates;

}