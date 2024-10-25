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
public class Employment {
    private String title;
    private String key_skill;
}
