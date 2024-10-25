package egov.digit.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static egov.digit.repository.ServiceQueryBuilder.*;

@Slf4j
@Repository
public class ServiceRequestRepository {


    @Autowired
    ServiceQueryBuilder serviceQueryBuilder;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ServiceRowMapper serviceRowMapper;

    @Autowired
    ServiceDefinitionQueryBuilder serviceDefinitionQueryBuilder;
    // Save service data to the service table
    public void createService(ServiceRequest serviceRequest) {

        Service service = serviceRequest.getService();

            jdbcTemplate.update(INSERT_SERVICE,
                    service.getId(),
                    service.getTenantId(),
                    service.getServiceDefId(),
                    service.getReferenceId(),
                    serviceRequest.getRequestInfo().getDid(),
                    serviceRequest.getRequestInfo().getDid(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    serviceDefinitionQueryBuilder.convertToJSONB(service.getAdditionalDetails()),  // JSON
                    service.getAccountId(),
                    service.getClientId()
            );


        // Save attributes data
        this.saveAttributeValues(service.getAttributes(), service.getId(), serviceRequest);
    }

    // Save attribute values to service_attribute_value table
    private void saveAttributeValues(List<AttributeValue> attributes, String referenceId, ServiceRequest serviceRequest) {
        for (AttributeValue attribute : attributes) {
                jdbcTemplate.update(INSERT_SERVICE_ATTRIBUTE,
                        attribute.getId(),
                        referenceId,  // Use the service's ID as the reference ID
                        attribute.getAttributeCode(),
                        serviceDefinitionQueryBuilder.convertToJSONB(attribute.getValue()),  // JSON for attribute values
                        serviceRequest.getRequestInfo().getDid(),
                        serviceRequest.getRequestInfo().getDid(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        serviceDefinitionQueryBuilder.convertToJSONB(attribute.getAdditionalDetails())  // JSON for additional details
                );
        }
    }

    public List<Service> getService(ServiceSearchRequest serviceSearchRequest) {
        ServiceCriteria criteria = serviceSearchRequest.getServiceCriteria();

        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getIds()) && ObjectUtils.isEmpty(criteria.getTenantId()) && CollectionUtils.isEmpty(criteria.getServiceDefIds()) && CollectionUtils.isEmpty(criteria.getReferenceIds()))
            return new ArrayList<>();

        //  Fetch ids based on criteria if ids are not present
        if(CollectionUtils.isEmpty(criteria.getIds())){
            // Fetch ids according to given criteria
            String idQuery = serviceQueryBuilder.getServiceIdsQuery(serviceSearchRequest, preparedStmtList);
            log.info("Service ids query: " + idQuery);
            log.info("Parameters: " + preparedStmtList.toString());
            List<String> serviceIds = jdbcTemplate.query(idQuery, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));

            if(CollectionUtils.isEmpty(serviceIds))
                return new ArrayList<>();

            // Set ids in criteria
            criteria.setIds(serviceIds);
            preparedStmtList.clear();
        }

        //  Search based on the ids found out/ ids been explicitly provided in the request.
        String query = serviceQueryBuilder.getServiceSearchQuery(criteria, preparedStmtList);
        log.info("query for search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), serviceRowMapper);

    }
}
