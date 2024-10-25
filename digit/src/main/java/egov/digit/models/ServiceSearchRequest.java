package egov.digit.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * ServiceSearchRequest
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSearchRequest {
    @JsonProperty("requestInfo")
    @NotNull
    @Valid
    private RequestInfo requestInfo = null;

    @JsonProperty("serviceDefinition")
    @NotNull
    @Valid
    private ServiceCriteria serviceCriteria = null;

    @JsonProperty("pagination")
    @Valid
    private Pagination pagination = null;


}
