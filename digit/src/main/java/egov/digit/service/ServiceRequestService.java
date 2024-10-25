package egov.digit.service;

import egov.digit.config.Configuration;
import egov.digit.repository.ServiceRequestRepository;
import egov.digit.utils.validators.ServiceRequestValidator;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import egov.digit.models.*;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static egov.digit.utils.Constants.VALUE;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceRequestValidator serviceRequestValidator;

    @Autowired
    private Configuration config;


    public egov.digit.models.Service createService(ServiceRequest serviceRequest) {

        egov.digit.models.Service service = serviceRequest.getService();

        // Validate incoming service definition request
        serviceRequestValidator.validateServiceRequest(serviceRequest);

        // Format incoming service definition request
        this.formatServiceRequest(serviceRequest);

        serviceRequestRepository.createService(serviceRequest);
        return service;
    }
    public List<egov.digit.models.Service> searchService(ServiceSearchRequest serviceSearchRequest){

        List<egov.digit.models.Service> listOfServices = serviceRequestRepository.getService(serviceSearchRequest);

        if(CollectionUtils.isEmpty(listOfServices))
            return new ArrayList<>();

        return listOfServices;
    }

    public void formatServiceRequest(ServiceRequest serviceRequest) {
        egov.digit.models.Service service = serviceRequest.getService();
        RequestInfo requestInfo = serviceRequest.getRequestInfo();

        // Format id for service
        service.setId(UUID.randomUUID().toString());

        // format details in attribute values
        service.getAttributes().forEach(attributeValue -> {
            attributeValue.setId(UUID.randomUUID().toString());
            attributeValue.setReferenceId(service.getId());
        });

    }

//    private Map<String, Object> convertAttributeValuesIntoJson(ServiceRequest serviceRequest) {
//        Map<String, Object> attributeCodeVsValueMap = new HashMap<>();
//        serviceRequest.getService().getAttributes().forEach(attributeValue -> {
//            attributeCodeVsValueMap.put(attributeValue.getAttributeCode(), attributeValue.getValue());
//            Map<String, Object> jsonObj = new HashMap<>();
//            jsonObj.put(VALUE, attributeValue.getValue());
//            attributeValue.setValue(jsonObj);
//        });
//        return attributeCodeVsValueMap;
//    }

}
