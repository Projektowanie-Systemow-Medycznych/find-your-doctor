CREATE TABLE doctor (
     id UUID PRIMARY KEY,
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