-- V1__create_codes_list_table.sql
CREATE TABLE codes_list (
    id VARCHAR PRIMARY KEY,
    metadata_label VARCHAR,
    metadata_version VARCHAR,
    external_link_version VARCHAR,
    search_config JSONB,
    content JSONB
);