package egov.digit.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import org.w3c.dom.Attr;

/**
 * Hold the Service field details as json object.
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("tenantId")
    @NotNull
    @Size(min = 2, max = 64)
    private String tenantId = null;

    @JsonProperty("serviceDefId")
    @NotNull
    @Size(min = 2, max = 64)
    private String serviceDefId = null;

    @JsonProperty("referenceId")
    @Size(min = 2, max = 64)
    private String referenceId = null;

    @JsonProperty("attributes")
    @NotNull
    @Valid
    private List<AttributeValue> attributes = new ArrayList<>();

    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;

    @JsonProperty("accountId")
    @NotNull
    @Size(max = 64)
    private String accountId = null;

    @JsonProperty("clientId")
    @Size(max = 64)
    private String clientId = null;

    @JsonProperty("code")
    @Size(max = 64)
    private String code = null;

    @JsonProperty("module")
    @Size(max = 64)
    private String module = null;


    public Service addAttributesItem(AttributeValue attributesItem) {
        this.attributes.add(attributesItem);
        return this;
    }

}
