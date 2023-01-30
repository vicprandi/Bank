CREATE SEQUENCE client_id_seq OWNED BY client.id;

ALTER TABLE client ALTER COLUMN id SET DEFAULT nextval('client_id_seq');

