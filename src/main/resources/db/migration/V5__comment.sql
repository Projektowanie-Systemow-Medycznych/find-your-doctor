CREATE TABLE comment (
    id UUID PRIMARY KEY,
    doctor_id UUID REFERENCES doctor(id),
    user_id UUID REFERENCES "user"(id),
    comment_text TEXT,
    rating INTEGER CHECK (rating BETWEEN 1 AND 5),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
