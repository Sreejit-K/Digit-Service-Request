package egov.digit.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import egov.digit.config.Configuration;
import egov.digit.models.*;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.List;

@Component
public class ServiceDefinitionQueryBuilder {

    @Autowired
    private Configuration config;

    private static final String SELECT = " SELECT ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String LEFT_JOIN  =  " LEFT OUTER JOIN ";
    private static final String AND_QUERY = " AND ";

    static final String INSERT_SERVICE_DEFINITION = "INSERT INTO service_definition (id, tenantId, code, isActive, createdBy, lastModifiedBy, createdTime, lastModifiedTime, additionalDetails, clientId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    static final String INSERT_SERVICE_ATTRIBUTE_DEFINITION = "INSERT INTO service_attribute_definition (id, referenceId, tenantId, code, dataType, values, isActive, required, regEx, \"order\", createdBy, lastModifiedBy, createdTime, lastModifiedTime, additionalDetails) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private static final String IDS_WRAPPER_QUERY = " SELECT id FROM ({HELPER_TABLE}) temp ";
    private final String ORDERBY_CREATEDTIME = " ORDER BY sd.createdtime DESC ";




    public String getServiceDefinitionsIdsQuery(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest, List<Object> preparedStmtList) {
        ServiceDefinitionCriteria criteria = serviceDefinitionSearchRequest.getServiceDefinitionCriteria();

        StringBuilder query = new StringBuilder(SELECT + " DISTINCT(sd.id), sd.createdtime ");
        query.append(" FROM service_definition sd ");

        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        if(!CollectionUtils.isEmpty(criteria.getCode())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.code IN ( ").append(createQuery(criteria.getCode())).append(" )");
            addToPreparedStatement(preparedStmtList, criteria.getCode());
        }


        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.id IN ( ").append(createQuery(criteria.getIds())).append(" )");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }

        if(!ObjectUtils.isEmpty(criteria.getClientId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.clientid = ? ");
            preparedStmtList.add(criteria.getClientId());
        }

        // Fetch service definitions which have NOT been soft deleted
        addClauseIfRequired(query, preparedStmtList);
        query.append(" sd.isActive = ? ");
        preparedStmtList.add(Boolean.TRUE);

        // order service definitions based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        // Pagination to limit results
        if(ObjectUtils.isEmpty(serviceDefinitionSearchRequest.getPagination())){
            prepareDefaultPaginationObject(serviceDefinitionSearchRequest);
        }
        addPagination(query, preparedStmtList, serviceDefinitionSearchRequest.getPagination());

        return IDS_WRAPPER_QUERY.replace("{HELPER_TABLE}", query.toString());
    }

    private void prepareDefaultPaginationObject(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        Pagination pagination = new Pagination();
        pagination.setOffset(config.getDefaultOffset());
        pagination.setLimit(config.getDefaultLimit());
        serviceDefinitionSearchRequest.setPagination(pagination);
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    private void addPagination(StringBuilder query, List<Object> preparedStmtList, Pagination pagination) {

        // Append offset
        query.append(" OFFSET ? ");
        preparedStmtList.add(ObjectUtils.isEmpty(pagination.getOffset()) ? config.getDefaultOffset() : pagination.getOffset());

        // Append limit
        query.append(" LIMIT ? ");
        preparedStmtList.add(ObjectUtils.isEmpty(pagination.getLimit()) ? config.getDefaultLimit() : pagination.getLimit());
    }


    public String getServiceDefinitionSearchQuery(ServiceDefinitionCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder("SELECT sd.id, sd.tenantid,  sd.code, sd.isactive, sd.createdby, sd.lastmodifiedby, sd.createdtime, sd.lastmodifiedtime, sd.additionaldetails, sd.clientid, "
                + "ad.id as attribute_definition_id, ad.referenceid as attribute_definition_referenceid,ad.tenantid as attribute_definition_tenantid, ad.code as attribute_definition_code, ad.datatype as attribute_definition_datatype, ad.values as attribute_definition_values, ad.isactive as attribute_definition_isactive, ad.required as attribute_definition_required, ad.regex as attribute_definition_regex, ad.order as attribute_definition_order, ad.createdby as attribute_definition_createdby, ad.lastmodifiedby as attribute_definition_lastmodifiedby, ad.createdtime as attribute_definition_createdtime, ad.lastmodifiedtime as attribute_definition_lastmodifiedtime, ad.additionaldetails as attribute_definition_additionaldetails "
                + "FROM service_definition as sd "
                + "INNER JOIN service_attribute_definition as ad ON "
                + "sd.id=ad.referenceid ");


        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sd.id IN ( ").append(createQuery(criteria.getIds())).append(" )");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }

        return query.toString();
    }

    public PGobject convertToJSONB(String valueToBeConverted) {
        PGobject jsonbObject = new PGobject();
        jsonbObject.setType("jsonb");
        try {
            jsonbObject.setValue(valueToBeConverted);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set jsonb value for additionalDetails", e);
        }
        return jsonbObject;
    }

    public PGobject convertToJSONB(Object objectToBeConverted) {

        String ObjectToString = null;
        try {
            ObjectMapper objectMapper =new ObjectMapper();
            ObjectToString = objectMapper.writeValueAsString(objectToBeConverted);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert additionalDetails to JSON", e);
        }

        PGobject jsonbObject = new PGobject();
        jsonbObject.setType("jsonb");
        try {
            jsonbObject.setValue( ObjectToString);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set jsonb value for additionalDetails", e);
        }
        return jsonbObject;
    }

}