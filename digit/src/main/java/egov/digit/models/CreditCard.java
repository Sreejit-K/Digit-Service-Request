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
public class CreditCard {
    private String cc_number;
}