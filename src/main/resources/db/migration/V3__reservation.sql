CREATE TABLE available_slots (
    id UUID PRIMARY KEY,
    datetime TIMESTAMP NOT NULL,
    address TEXT,
    doctor_id UUID REFERENCES doctor(id),
    is_reserved BOOLEAN DEFAULT FALSE
);

CREATE TABLE reservation (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES "user"(id),
    slot_id UUID REFERENCES available_slots(id) NOT NULL
);