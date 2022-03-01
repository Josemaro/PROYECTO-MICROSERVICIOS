INSERT INTO tbl_movement_types (id, name) VALUES (1, 'withdrawal');
INSERT INTO tbl_movement_types (id, name) VALUES (2, 'deposit');

INSERT INTO tbl_account_types (id, name) VALUES (1, 'savings account');
INSERT INTO tbl_account_types (id, name) VALUES (2, 'current account');
INSERT INTO tbl_account_types (id, name) VALUES (3, 'fixed term account');

INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (1, '4213550000001111', 800, 15, 30, 'CREATED', 1);
INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (2, '4213550011112222', 950, 30, 50, 'CREATED', 1);
INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (3, '4213550022223333', 800, 15, 30, 'CREATED', 1);
INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (4, '4213550033334444', 950, 30, 50, 'CREATED', 2);
INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (5, '4213550055556666', 950, 30, 50, 'CREATED', 2);


INSERT INTO tbl_account_owners (id, customer_id, account_id) values (1,1,1);
INSERT INTO tbl_account_owners (id, customer_id, account_id) values (2,2,2);
INSERT INTO tbl_account_owners (id, customer_id, account_id) values (3,3,3);
INSERT INTO tbl_account_owners (id, customer_id, account_id) values (4,4,4);
INSERT INTO tbl_account_owners (id, customer_id, account_id) values (5,5,5);

INSERT INTO tbl_account_signers (id, customer_id, account_id) values (1,5,4);
