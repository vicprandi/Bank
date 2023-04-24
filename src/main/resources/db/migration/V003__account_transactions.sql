CREATE TABLE account_transactions(
    id BIGSERIAL PRIMARY KEY,
    origin_account_id BIGSERIAL NOT NULL,
    destination_account_id BIGINT NULL,
    value NUMERIC NOT NULL,
    transaction_type VARCHAR(50),


    CONSTRAINT fk_origin_account_id
        FOREIGN KEY (origin_account_id)
        REFERENCES account(id),

    CONSTRAINT fk_destination_account_id
        FOREIGN KEY (destination_account_id)
        REFERENCES account(id)
);