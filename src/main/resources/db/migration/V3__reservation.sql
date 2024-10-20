CREATE TABLE reservation (
    id UUID PRIMARY KEY,
    datetime TIMESTAMP,
    address TEXT,
    user_id UUID REFERENCES "user"(id),
    doctor_id UUID REFERENCES doctor(id)
);