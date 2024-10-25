package egov.digit.utils.validators;


import egov.digit.config.Configuration;
import egov.digit.models.*;
import egov.digit.repository.ServiceDefinitionRequestRepository;
import egov.digit.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static egov.digit.utils.ErrorCode.*;


@Slf4j
@Component
public class ServiceRequestValidator {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceDefinitionRequestRepository serviceDefinitionRequestRepository;

    @Autowired
    private Configuration config;

    public void validateServiceRequest(ServiceRequest serviceRequest){
        List<ServiceDefinition> serviceDefinitions = validateServiceDefID(serviceRequest.getService().getTenantId(), serviceRequest.getService().getServiceDefId());
        serviceRequest.getService().setCode(serviceDefinitions.get(0).getCode());
        serviceRequest.getService().setAdditionalDetails(serviceDefinitions.get(0).getAdditionalDetails());
    }

    private List<ServiceDefinition> validateServiceDefID(String tenantId, String serviceDefId) {
        List<ServiceDefinition> serviceDefinitions = serviceDefinitionRequestRepository.getServiceDefinitions(ServiceDefinitionSearchRequest.builder().serviceDefinitionCriteria(ServiceDefinitionCriteria.builder().tenantId(tenantId).ids(Arrays.asList(serviceDefId)).build()).build());

        if(serviceDefinitions.isEmpty())
            throw new CustomException(SERVICE_REQUEST_INVALID_SERVICE_DEF_ID_CODE, SERVICE_REQUEST_INVALID_SERVICE_DEF_ID_MSG);

        return serviceDefinitions;
    }

}