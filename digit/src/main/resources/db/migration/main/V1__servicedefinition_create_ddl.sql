CREATE TABLE service_definition(
    id character varying(64),
    tenantId character varying(64),
    code character varying(64),
    isActive boolean,
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    additionalDetails JSONB,
    clientId character varying(64),
    CONSTRAINT uk_service_definition UNIQUE (id),
    CONSTRAINT pk_service_definition PRIMARY KEY (tenantId,code)
);

CREATE TABLE service_attribute_definition(
    id character varying(64),
    referenceId character varying(64),
    tenantId character varying(64),
    code character varying(64),
    dataType character varying(64),
    "values" character varying(8192),
    isActive boolean,
    required boolean,
    regEx character varying(64),
    "order" character varying(64),
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    additionalDetails JSONB,
    CONSTRAINT uk_attribute_definition UNIQUE (id),
    CONSTRAINT pk_attribute_definition PRIMARY KEY (code,referenceId)
);