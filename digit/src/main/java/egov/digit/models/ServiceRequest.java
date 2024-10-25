package egov.digit.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;
import org.egov.common.contract.request.RequestInfo;
import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * ServiceRequest
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequest {
    @JsonProperty("requestInfo")
    @NotNull
    @Valid
    private RequestInfo requestInfo = null;

    @JsonProperty("service")
    @Valid
    private Service service = null;

}