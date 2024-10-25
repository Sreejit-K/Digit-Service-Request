package egov.digit.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import egov.digit.models.AttributeValue;
import egov.digit.models.Service;
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

import static egov.digit.utils.Constants.VALUE_JSON_PATH;


@Component
public class ServiceRowMapper implements ResultSetExtractor<List<Service>> {

    /**
     * Rowmapper that maps every column of the search result set to a key in the model.
     */

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Service> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Service> serviceMap = new HashMap<>();
        while (rs.next()) {
            String currentId = rs.getString("id");
            Service currentService = serviceMap.get(currentId);
            if (!StringUtils.isBlank(currentId)) {

                if (currentService == null) {

                    currentService = Service.builder()
                            .id(rs.getString("id"))
                            .tenantId(rs.getString("tenantid"))
                            .serviceDefId(rs.getString("servicedefid"))
                            .referenceId(rs.getString("referenceid"))
                            .additionalDetails(getAdditionalDetail((PGobject) rs.getObject("additionaldetails")))
                            .accountId(rs.getString("accountid"))
                            .clientId(rs.getString("clientid"))
                            .build();

                }

                addAttributeValues(rs, currentService);
                serviceMap.put(currentId, currentService);
            }

        }
        return new ArrayList<>(serviceMap.values());
    }

    private void addAttributeValues(ResultSet rs, Service service) throws SQLException, DataAccessException {
        PGobject genericValueObject = (PGobject) rs.getObject("attribute_value_value");

       AttributeValue attributeValue = AttributeValue.builder().id(rs.getString("attribute_value_id"))
                .referenceId(rs.getString("attribute_value_referenceid"))
                .attributeCode(rs.getString("attribute_value_attributecode"))
                .value(getProperTypeCastedAttributeValue(genericValueObject))
                .additionalDetails(getAdditionalDetail((PGobject) rs.getObject("attribute_value_additionaldetails")))
                .build();

        if (CollectionUtils.isEmpty(service.getAttributes())) {
            List<AttributeValue> attributeValues = new ArrayList<>();
            attributeValues.add(attributeValue);
            service.setAttributes(attributeValues);
        } else {
            service.getAttributes().add(attributeValue);
        }


    }

    private Object getProperTypeCastedAttributeValue(PGobject genericValueObject) {

        if (genericValueObject == null || genericValueObject.getValue() == null) {
            return null; // Return null or a default value
        }

        String valueJson = genericValueObject.getValue();

        // Check if the JSON is empty
        if ("{}".equals(valueJson.trim())) {
            return Collections.emptyMap(); // or return null, based on requirements
        }

        try {
            return JsonPath.read(valueJson, VALUE_JSON_PATH);
        } catch (PathNotFoundException e) {
            throw new CustomException("PATH_NOT_FOUND", "The specified path was not found in the JSON structure");
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