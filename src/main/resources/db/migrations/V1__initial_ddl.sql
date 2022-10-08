CREATE TABLE file
(
    id            uuid NOT NULL,
    content       bytea,
    date_added    TIMESTAMP,
    date_modified TIMESTAMP,
    kind          VARCHAR(255),
    name          VARCHAR(255),
    size          VARCHAR(255),
    CONSTRAINT file_pkey PRIMARY KEY (id)
)