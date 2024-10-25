CREATE TABLE service(
    id character varying(64),
    tenantId character varying(64),
    serviceDefId character varying(64),
    referenceId character varying(64),
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    additionalDetails JSONB,
    accountId character varying(64),
    clientId character varying(64),
    CONSTRAINT uk_service UNIQUE (id)
);

CREATE TABLE service_attribute_value(
    id character varying(64),
    referenceId character varying(64),
    attributeCode character varying(64),
    "value" JSONB,
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    additionalDetails JSONB,
    CONSTRAINT uk_attribute_value UNIQUE (id)
);