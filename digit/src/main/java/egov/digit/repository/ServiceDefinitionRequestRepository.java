package egov.digit.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.models.*;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static egov.digit.repository.ServiceDefinitionQueryBuilder.*;

@Slf4j
@Repository
public class ServiceDefinitionRequestRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceDefinitionRowMapper serviceDefinitionRowMapper;

    @Autowired
    private ServiceDefinitionQueryBuilder serviceDefinitionQueryBuilder;

    public void addServiceDefinition(ServiceDefinitionRequest serviceDefinitionRequest) {

        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();
        PGobject jsonbObjectOfAdditionalDetails = serviceDefinitionQueryBuilder.convertToJSONB(serviceDefinition.getAdditionalDetails());
        // Insert Service Definition
        jdbcTemplate.update(INSERT_SERVICE_DEFINITION,
                serviceDefinition.getId(),
                serviceDefinition.getTenantId(),
                serviceDefinition.getCode(),
                serviceDefinition.getIsActive(),
                serviceDefinitionRequest.getRequestInfo().getDid()  ,
                serviceDefinitionRequest.getRequestInfo().getDid() ,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                jsonbObjectOfAdditionalDetails,
                serviceDefinition.getClientId());

        List<AttributeDefinition> attributeDefinitions = serviceDefinitionRequest.getServiceDefinition().getAttributes();
        // Insert Service Attribute Definitions
        for (AttributeDefinition attribute : attributeDefinitions) {
            ObjectMapper objectMapper = new ObjectMapper();
            PGobject jsonbObjectOfAttributeAdditionalDetails = serviceDefinitionQueryBuilder.convertToJSONB(serviceDefinition.getAdditionalDetails());
            try {
                jdbcTemplate.update(INSERT_SERVICE_ATTRIBUTE_DEFINITION,
                        attribute.getId(),
                        serviceDefinition.getId(), // referenceId linking to the service definition
                        attribute.getTenantId(),
                        attribute.getCode(),
                        attribute.getDataType(),
                        objectMapper.writeValueAsString(attribute.getValues()),
                        attribute.getIsActive(),
                        attribute.getRequired(),
                        attribute.getRegex(),
                        attribute.getOrder(),
                        serviceDefinitionRequest.getRequestInfo().getDid(),
                        serviceDefinitionRequest.getRequestInfo().getDid(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        jsonbObjectOfAttributeAdditionalDetails);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<ServiceDefinition> getServiceDefinitions(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        ServiceDefinitionCriteria criteria = serviceDefinitionSearchRequest.getServiceDefinitionCriteria();

        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getIds()) && ObjectUtils.isEmpty(criteria.getTenantId()) && CollectionUtils.isEmpty(criteria.getCode()))
            return new ArrayList<>();

        // Fetch ids based on criteria if ids are not present
        if(CollectionUtils.isEmpty(criteria.getIds())){
            // Fetch ids according to given criteria
            String idQuery = serviceDefinitionQueryBuilder.getServiceDefinitionsIdsQuery(serviceDefinitionSearchRequest, preparedStmtList);
            log.info("Service definition ids query: " + idQuery);
            log.info("Parameters: " + preparedStmtList.toString());
            List<String> serviceDefinitionIds = jdbcTemplate.query(idQuery, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));

            if(CollectionUtils.isEmpty(serviceDefinitionIds))
                return new ArrayList<>();

            // Set ids in criteria
            criteria.setIds(serviceDefinitionIds);
            preparedStmtList.clear();
        }

        String query = serviceDefinitionQueryBuilder.getServiceDefinitionSearchQuery(criteria, preparedStmtList);
        log.info("query for search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), serviceDefinitionRowMapper);

    }

}