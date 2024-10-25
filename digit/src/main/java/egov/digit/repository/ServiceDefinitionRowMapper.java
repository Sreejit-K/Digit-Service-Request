package egov.digit.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.models.AttributeDefinition;
import egov.digit.models.ServiceDefinition;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class ServiceDefinitionRowMapper implements ResultSetExtractor<List<ServiceDefinition>> {

    /**
     * Rowmapper that maps every column of the search result set to a key in the model.
     */

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<ServiceDefinition> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ServiceDefinition> serviceDefinitionMap = new HashMap<>();
        while (rs.next()) {
            String currentId = rs.getString("id");
            ServiceDefinition currentServiceDefinition = serviceDefinitionMap.get(currentId);
            if (!StringUtils.isBlank(currentId)) {

                if (currentServiceDefinition == null) {

                    currentServiceDefinition = ServiceDefinition.builder()
                            .id(rs.getString("id"))
                            .tenantId(rs.getString("tenantid"))
                            .code(rs.getString("code"))
                            .isActive(rs.getBoolean("isactive"))
                            .additionalDetails(getAdditionalDetail((PGobject) rs.getObject("additionaldetails")))
                            .clientId(rs.getString("clientid"))
                            .build();

                }

                addAttributeDefinitions(rs, currentServiceDefinition);
                serviceDefinitionMap.put(currentId, currentServiceDefinition);
            }

        }
        return new ArrayList<>(serviceDefinitionMap.values());
    }

    private void addAttributeDefinitions(ResultSet rs, ServiceDefinition serviceDefinition) throws SQLException, DataAccessException {
        String values = rs.getString("attribute_definition_values");
        AttributeDefinition attributeDefinition = AttributeDefinition.builder().id(rs.getString("attribute_definition_id"))
                .referenceId(rs.getString("attribute_definition_referenceid"))
                .tenantId(rs.getString("attribute_definition_tenantid"))
                .code(rs.getString("attribute_definition_code"))
                .dataType(rs.getString("attribute_definition_datatype"))
                .values(StringUtils.isEmpty(values) ? Arrays.asList() : Arrays.asList(values.split(",")))
                .isActive(rs.getBoolean("attribute_definition_isactive"))
                .required(rs.getBoolean("attribute_definition_required"))
                .regex(rs.getString("attribute_definition_regex"))
                .order(rs.getString("attribute_definition_order"))
                .additionalDetails(getAdditionalDetail((PGobject) rs.getObject("attribute_definition_additionaldetails")))
                .build();

        if (CollectionUtils.isEmpty(serviceDefinition.getAttributes())) {
            List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            attributeDefinitions.add(attributeDefinition);
            serviceDefinition.setAttributes(attributeDefinitions);
        } else {
            serviceDefinition.getAttributes().add(attributeDefinition);
        }


    }

    private JsonNode getAdditionalDetail(PGobject pGobject){

        JsonNode additionalDetail = null;
        try {
            if(pGobject != null){
                additionalDetail = mapper.readTree(pGobject.getValue());
            }
        }
        catch (IOException e){
            throw new CustomException("PARSING_ERROR","Failed to parse additionalDetail object");
        }
        return additionalDetail;
    }

}
