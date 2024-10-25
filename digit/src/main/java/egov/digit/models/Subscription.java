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
public class Subscription {
    private String plan;
    private String status;
    private String payment_method;
    private String term;
}