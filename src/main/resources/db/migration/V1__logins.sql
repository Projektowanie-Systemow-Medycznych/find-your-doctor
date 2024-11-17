CREATE TABLE Picture (
    id UUID PRIMARY KEY,
    data BYTEA NOT NULL
);

CREATE TABLE doctor (
     id UUID PRIMARY KEY,
     picture_id UUID REFERENCES Picture(id),
     login TEXT,
     password TEXT,
     name TEXT
);

CREATE TABLE "user" (
     id UUID PRIMARY KEY,
     login TEXT,
     password TEXT,
     name TEXT
);