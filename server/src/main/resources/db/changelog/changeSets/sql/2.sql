-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2018-03-23 07:38:56.934

-- tables
-- Table: file
CREATE TABLE file (
    id bigint  NOT NULL,
    name text  NULL,
    contents bytea  NOT NULL,
    CONSTRAINT file_pk PRIMARY KEY (id)
);

-- End of file.

