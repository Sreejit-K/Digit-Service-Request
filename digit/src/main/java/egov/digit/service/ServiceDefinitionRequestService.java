package egov.digit.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.models.*;
import egov.digit.repository.ServiceDefinitionRequestRepository;
import egov.digit.utils.validators.ServiceDefinitionRequestValidator;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceDefinitionRequestService {

    @Autowired
    private ServiceDefinitionRequestRepository serviceDefinitionRequestRepository;

    @Autowired
    private ServiceDefinitionRequestValidator serviceDefinitionRequestValidator;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;


    public ServiceDefinition createServiceDefinition(ServiceDefinitionRequest serviceDefinitionRequest)  {

        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();

        // Validate incoming service definition request
        serviceDefinitionRequestValidator.validateServiceDefinitionRequest(serviceDefinitionRequest);
        // Format incoming service definition request
        this.formatServiceDefinitionRequest(serviceDefinitionRequest);

        // Fetch additional details from the User API
        try {
            User user = restTemplate.getForObject(apiUrl, User.class);
            ObjectMapper objectMapper = new ObjectMapper();

            if (user != null) {
                serviceDefinitionRequest.getServiceDefinition().setAdditionalDetails(user.getAddress());
            } else {
                serviceDefinitionRequest.getServiceDefinition().setAdditionalDetails(new Address());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(serviceDefinitionRequest);
        serviceDefinitionRequestRepository.addServiceDefinition(serviceDefinitionRequest);
        return serviceDefinition;
    }

    public List<ServiceDefinition> searchServiceDefinition(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest){

        List<ServiceDefinition> listOfServiceDefinitions = serviceDefinitionRequestRepository.getServiceDefinitions(serviceDefinitionSearchRequest);
        if(CollectionUtils.isEmpty(listOfServiceDefinitions))
            return new ArrayList<>();
        return listOfServiceDefinitions;
    }


    public void formatServiceDefinitionRequest(ServiceDefinitionRequest serviceDefinitionRequest) {
        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();
        RequestInfo requestInfo = serviceDefinitionRequest.getRequestInfo();

        // generate ID for service definition
        serviceDefinition.setId(UUID.randomUUID().toString());

        // generate details for attributes
        serviceDefinition.getAttributes().forEach(attribute -> {
            attribute.setId(UUID.randomUUID().toString());
            attribute.setReferenceId(serviceDefinition.getId());
        });

        // Initialize values with empty strings in case of non-list type attribute definition values
        serviceDefinition.getAttributes().forEach(attributeDefinition -> {
            if(!attributeDefinition.getDataType().equals("String")){
                List<String> emptyStringList = new ArrayList<>();
                emptyStringList.add("");
                attributeDefinition.setValues(emptyStringList);
            }
        });

    }

}

