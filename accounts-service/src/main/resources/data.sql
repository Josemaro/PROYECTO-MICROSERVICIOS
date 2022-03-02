INSERT INTO tbl_movement_types (id, name) VALUES (1, 'withdrawal');
INSERT INTO tbl_movement_types (id, name) VALUES (2, 'deposit');

INSERT INTO tbl_account_types (id, name) VALUES (1, 'savings account');
INSERT INTO tbl_account_types (id, name) VALUES (2, 'current account');
INSERT INTO tbl_account_types (id, name) VALUES (3, 'fixed term account');

INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550000001111', 800, 15,'2020-02-01', 15, 'CREATED', 1);
INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550011112222', 950, 30,'2020-02-05', 15, 'CREATED', 1);
INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550022223333', 800, 15,'2020-02-10', 15, 'CREATED', 1);
INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550033334444', 950, 30,'2020-02-28', 99999, 'CREATED', 2);
INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550055556666', 950, 30,'2020-03-01', 99999, 'CREATED', 2);
INSERT INTO tbl_accounts (account_number, balance, commission, create_at, movements_limit, state, account_type_id ) VALUES ('4213550066667777', 950, 30,'2020-03-01', 99999, 'CREATED', 2);

INSERT INTO tbl_account_owners (customer_id, account_id) values (1,1);
INSERT INTO tbl_account_owners (customer_id, account_id) values (2,2);
INSERT INTO tbl_account_owners (customer_id, account_id) values (3,3);
INSERT INTO tbl_account_owners (customer_id, account_id) values (4,4);
INSERT INTO tbl_account_owners (customer_id, account_id) values (4,5);
INSERT INTO tbl_account_owners (customer_id, account_id) values (5,5);
INSERT INTO tbl_account_owners (customer_id, account_id) values (4,6);

INSERT INTO tbl_account_signers (customer_id, account_id) values (4,5);
INSERT INTO tbl_account_signers (customer_id, account_id) values (5,5);

INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (100,'2022-02-01',1,1);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (150,'2022-02-01',1,1);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (200,'2022-02-01',1,1);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (170,'2022-03-01',1,1);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (250,'2022-03-01',1,2);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (250,'2022-03-01',1,2);
INSERT INTO tbl_movements (amount,movement_date,account_id,movement_type_id) values (50,'2022-03-01',1,2);
