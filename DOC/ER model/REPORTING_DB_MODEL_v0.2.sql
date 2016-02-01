
/* Drop Indexes */

DROP INDEX IF EXISTS idx_report_id;
DROP INDEX IF EXISTS idx_report_execution_log_id;



/* Drop Tables 
*/
DROP TABLE IF EXISTS reporting.report_execution_log;
DROP TABLE IF EXISTS reporting.report;



/* Drop Sequences 

DROP SEQUENCE IF EXISTS reporting.report_execution_log_id_seq;
DROP SEQUENCE IF EXISTS reporting.report_id_seq;
*/

/* Create Sequences 

CREATE SEQUENCE reporting.report_execution_log_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reporting.report_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
*/

/* Create Tables */

CREATE TABLE reporting.report
(
	-- Unique primary key of the user defined areas
	id bigserial NOT NULL UNIQUE,
	-- Name of the report specified by the User
	name varchar(255) NOT NULL,
	-- Report description
	description varchar,
	-- Filter expression
	filter_expression text NOT NULL,
	-- It describes the frontend report components that should be displayed (e.g. ['spatial', 'vms', 'ers'])
	out_components varchar(255) NOT NULL,
	is_active boolean DEFAULT 'TRUE' NOT NULL,
	scope_id bigint NOT NULL,
	-- username of the user who has created and 'owns' the report
	created_by varchar(255) NOT NULL,
	-- Creation date/time of the report
	created_on timestamp NOT NULL,
	is_shared boolean DEFAULT 'FALSE' NOT NULL,
	is_deleted boolean DEFAULT 'FALSE' NOT NULL,
	deleted_on timestamp,
	deleted_by varchar(255),
	CONSTRAINT report_pkey PRIMARY KEY (id)
) WITHOUT OIDS;


CREATE TABLE reporting.report_execution_log
(
	id bigserial NOT NULL,
	-- The username of the user who has executed the particular report
	executed_by varchar(255) NOT NULL,
	executed_on timestamp NOT NULL,
	-- Unique primary key of the user defined areas
	report_id bigint NOT NULL UNIQUE,
	PRIMARY KEY (id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE reporting.report_execution_log
	ADD CONSTRAINT fk_report_id FOREIGN KEY (report_id)
	REFERENCES reporting.report (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;



/* Create Indexes */

CREATE INDEX idx_report_id ON reporting.report USING BTREE (id);
CREATE INDEX idx_report_execution_log_id ON reporting.report_execution_log USING BTREE (id);



/* Comments */

COMMENT ON COLUMN reporting.report.id IS 'Unique primary key of the user defined areas';
COMMENT ON COLUMN reporting.report.name IS 'Name of the report specified by the User';
COMMENT ON COLUMN reporting.report.description IS 'Report description';
COMMENT ON COLUMN reporting.report.filter_expression IS 'Filter expression';
COMMENT ON COLUMN reporting.report.out_components IS 'It describes the frontend report components that should be displayed (e.g. [''spatial'', ''vms'', ''ers''])';
COMMENT ON COLUMN reporting.report.created_by IS 'username of the user who has created and ''owns'' the report';
COMMENT ON COLUMN reporting.report.created_on IS 'Creation date/time of the report';
COMMENT ON COLUMN reporting.report_execution_log.executed_by IS 'The username of the user who has executed the particular report';
COMMENT ON COLUMN reporting.report_execution_log.report_id IS 'Unique primary key of the user defined areas';



