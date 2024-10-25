package egov.digit.controller;


import egov.digit.models.ServiceDefinition;
import egov.digit.models.ServiceDefinitionRequest;
import egov.digit.models.ServiceDefinitionResponse;
import egov.digit.models.ServiceDefinitionSearchRequest;
import egov.digit.service.ServiceDefinitionRequestService;
import egov.digit.utils.ResponseInfoManager;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/service")
public class ServiceDefinitionController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResponseInfoManager responseInfoFactory;

    @Autowired
    private ServiceDefinitionRequestService serviceDefinitionRequestService;

    @RequestMapping(value="/definition/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<ServiceDefinitionResponse> create(@RequestBody @Valid ServiceDefinitionRequest serviceDefinitionRequest) {
        ServiceDefinition serviceDefinition = serviceDefinitionRequestService.createServiceDefinition(serviceDefinitionRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(serviceDefinitionRequest.getRequestInfo(), true);
        ServiceDefinitionResponse response = ServiceDefinitionResponse.builder().serviceDefinition(Collections.singletonList(serviceDefinition)).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/definition/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<ServiceDefinitionResponse> search(@Valid @RequestBody ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        List<ServiceDefinition> serviceDefinitionList = serviceDefinitionRequestService.searchServiceDefinition(serviceDefinitionSearchRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(serviceDefinitionSearchRequest.getRequestInfo(), true);
        ServiceDefinitionResponse response  = ServiceDefinitionResponse.builder().serviceDefinition(serviceDefinitionList).responseInfo(responseInfo).pagination(serviceDefinitionSearchRequest.getPagination()).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}