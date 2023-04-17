DROP TABLE IF EXISTS requests;

CREATE TABLE requests
(
    "id"        int          NOT NULL,
    app         varchar(50)  NOT NULL,
    "uri"       varchar(255) NOT NULL,
    ip          varchar(50)  NOT NULL,
    "timestamp" timestamp    NOT NULL,
    CONSTRAINT PK_1 PRIMARY KEY ("id")
);