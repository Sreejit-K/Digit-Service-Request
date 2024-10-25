package egov.digit.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * ServiceResponse
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResponse {
    @JsonProperty("responseInfo")
    @Valid
    private ResponseInfo responseInfo = null;

    @JsonProperty("service")
    @Valid
    private List<Service> service = null;

    @JsonProperty("pagination")
    @Valid
    private Pagination pagination = null;


    public ServiceResponse addServiceItem(Service serviceItem) {
        if (this.service == null) {
            this.service = new ArrayList<>();
        }
        this.service.add(serviceItem);
        return this;
    }

}