package egov.digit.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * Hold the attribute details as object.
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeValue {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("referenceId")
    @Size(min = 2, max = 64)
    private String referenceId = null;

    @JsonProperty("attributeCode")
    @NotNull
    private String attributeCode = null;

    @JsonProperty("value")
    @NotNull
    private Object value = null;

    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;


}
