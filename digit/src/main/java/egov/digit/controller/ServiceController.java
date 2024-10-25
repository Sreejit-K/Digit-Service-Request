package egov.digit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.models.Service;
import egov.digit.models.ServiceRequest;
import egov.digit.models.ServiceResponse;
import egov.digit.models.ServiceSearchRequest;
import egov.digit.service.ServiceRequestService;
import egov.digit.utils.ResponseInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResponseInfoManager responseInfoManager;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @RequestMapping(value="/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> create(@RequestBody @Valid ServiceRequest serviceRequest) {
        Service service = serviceRequestService.createService(serviceRequest);
        ResponseInfo responseInfo = responseInfoManager.createResponseInfoFromRequestInfo(serviceRequest.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().service(Collections.singletonList(service)).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> search(@Valid @RequestBody ServiceSearchRequest serviceSearchRequest) {
        List<Service> serviceList = serviceRequestService.searchService(serviceSearchRequest);
        ResponseInfo responseInfo = responseInfoManager.createResponseInfoFromRequestInfo(serviceSearchRequest.getRequestInfo(), true);
        ServiceResponse response  = ServiceResponse.builder().service(serviceList).responseInfo(responseInfo).pagination(serviceSearchRequest.getPagination()).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
