
/* Drop Indexes */

DROP INDEX IF EXISTS idx_report_id;
DROP INDEX IF EXISTS idx_report_configuration_id;
DROP INDEX IF EXISTS idx_report_configuration_report_id;
DROP INDEX IF EXISTS idx_report_execution_log_report_configuration_id;
DROP INDEX IF EXISTS idx_report_execution_log_id;



/* Drop Tables */

DROP TABLE IF EXISTS reporting.config_scope_mapping;
DROP TABLE IF EXISTS reporting.report_execution_log;
DROP TABLE IF EXISTS reporting.report_configuration;
DROP TABLE IF EXISTS reporting.report;



/* Drop Sequences */

DROP SEQUENCE IF EXISTS reporting.area_group_id_seq;
DROP SEQUENCE IF EXISTS reporting.area_group_mapping_area_id_seq;
DROP SEQUENCE IF EXISTS reporting.area_status_id_seq;
DROP SEQUENCE IF EXISTS reporting.area_types_id_seq;
DROP SEQUENCE IF EXISTS reporting.eez_gid_seq;
DROP SEQUENCE IF EXISTS reporting.fao_id_seq;
DROP SEQUENCE IF EXISTS reporting.gfcm_id_seq;
DROP SEQUENCE IF EXISTS reporting.layer_group_id_seq;
DROP SEQUENCE IF EXISTS reporting.layer_id_seq;
DROP SEQUENCE IF EXISTS reporting.rac_id_seq;
DROP SEQUENCE IF EXISTS reporting.report_id_seq;
DROP SEQUENCE IF EXISTS reporting.report_layer_mapping_id_seq;
DROP SEQUENCE IF EXISTS reporting.rfmo_id_seq;
DROP SEQUENCE IF EXISTS reporting.spatial_configuration_id_seq;
DROP SEQUENCE IF EXISTS reporting.stat_rect_id_seq;
DROP SEQUENCE IF EXISTS reporting.user_areas_id_seq;




/* Create Sequences */

CREATE SEQUENCE reporting.area_group_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.area_group_mapping_area_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.area_status_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.area_types_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.eez_gid_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 249 CACHE 1;
CREATE SEQUENCE reporting.fao_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.gfcm_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.layer_group_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.layer_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.rac_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.report_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.report_layer_mapping_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.rfmo_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.spatial_configuration_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.stat_rect_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.user_areas_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;



/* Create Tables */

-- to check with cedric if we link it to report or report version
CREATE TABLE config_scope_mapping
(
	report_configuration_id bigint NOT NULL,
	scope_id varchar NOT NULL
) WITHOUT OIDS;


CREATE TABLE reporting.report
(
	-- Unique primary key of the user defined areas
	id bigserial NOT NULL UNIQUE,
	-- username of the user who has created and 'owns' the report
	created_by varchar(255) NOT NULL,
	is_deleted boolean DEFAULT 'FALSE' NOT NULL,
	deleted_on timestamp,
	CONSTRAINT report_pkey PRIMARY KEY (id)
) WITHOUT OIDS;


CREATE TABLE reporting.report_configuration
(
	id bigserial NOT NULL UNIQUE,
	-- Unique primary key of the user defined areas
	report_id bigint NOT NULL,
	-- Name of the report specified by the User
	name varchar(255) NOT NULL,
	-- Report description
	description varchar,
	-- Filter expression
	filter_expression text NOT NULL,
	-- It describes the frontend report components that should be displayed (e.g. ['spatial', 'vms', 'ers'])
	out_components varchar(255) NOT NULL,
	is_active boolean DEFAULT 'TRUE' NOT NULL,
	-- Creation date/time of the report
	created_on timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


CREATE TABLE reporting.report_execution_log
(
	id bigserial NOT NULL,
	report_configuration_id bigint NOT NULL UNIQUE,
	-- The username of the user who has executed the particular report
	executed_by varchar(255) NOT NULL,
	executed_on timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE reporting.report_configuration
	ADD CONSTRAINT filter_rep_id_fkey FOREIGN KEY (report_id)
	REFERENCES reporting.report (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE reporting.report_execution_log
	ADD FOREIGN KEY (report_configuration_id)
	REFERENCES reporting.report_configuration (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;



/* Create Indexes */

CREATE INDEX idx_report_id ON reporting.report USING BTREE (id);
CREATE INDEX idx_report_configuration_id ON reporting.report_configuration USING BTREE (id);
CREATE INDEX idx_report_configuration_report_id ON reporting.report_configuration USING BTREE (report_id);
CREATE INDEX idx_report_execution_log_report_configuration_id ON reporting.report_execution_log USING BTREE (report_configuration_id);
CREATE INDEX idx_report_execution_log_id ON reporting.report_execution_log USING BTREE (id);



/* Comments */

COMMENT ON TABLE config_scope_mapping IS 'to check with cedric if we link it to report or report version';
COMMENT ON COLUMN reporting.report.id IS 'Unique primary key of the user defined areas';
COMMENT ON COLUMN reporting.report.created_by IS 'username of the user who has created and ''owns'' the report';
COMMENT ON COLUMN reporting.report_configuration.report_id IS 'Unique primary key of the user defined areas';
COMMENT ON COLUMN reporting.report_configuration.name IS 'Name of the report specified by the User';
COMMENT ON COLUMN reporting.report_configuration.description IS 'Report description';
COMMENT ON COLUMN reporting.report_configuration.filter_expression IS 'Filter expression';
COMMENT ON COLUMN reporting.report_configuration.out_components IS 'It describes the frontend report components that should be displayed (e.g. [''spatial'', ''vms'', ''ers''])';
COMMENT ON COLUMN reporting.report_configuration.created_on IS 'Creation date/time of the report';
COMMENT ON COLUMN reporting.report_execution_log.executed_by IS 'The username of the user who has executed the particular report';



