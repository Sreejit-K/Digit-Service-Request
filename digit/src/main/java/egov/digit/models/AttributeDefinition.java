package egov.digit.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * Hold the attribute definition fields details as json object.
 */
@Schema(description = "Hold the attribute definition fields details as json object.")
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeDefinition {
    @JsonProperty("id")
    @Size(min = 2, max = 64)
    private String id = null;

    @JsonProperty("referenceId")
    @Size(min = 2, max = 64)
    private String referenceId = null;

    @JsonProperty("tenantId")
    @Size(min = 2, max = 64)
    private String tenantId = null;

    @JsonProperty("code")
    @NotNull
    @Size(min = 2, max = 64)
    private String code = null;

    @JsonProperty("dataType")
    @NotNull
    private String dataType = null;

    @JsonProperty("values")
    private List<String> values = null;

    @JsonProperty("isActive")
    @NotNull
    private Boolean isActive = true;

    @JsonProperty("required")
    private Boolean required = null;

    @JsonProperty("regEx")
    @Size(min = 2, max = 64)
    private String regex = null;

    @JsonProperty("order")
    private String order = null;

    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;


    public AttributeDefinition addValuesItem(String valuesItem) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        this.values.add(valuesItem);
        return this;
    }

}
