INSERT INTO tbl_movement_types (id, name) VALUES (1, 'withdrawal');
INSERT INTO tbl_movement_types (id, name) VALUES (2, 'deposit');

INSERT INTO tbl_account_types (id, name) VALUES (1, 'savings account');
INSERT INTO tbl_account_types (id, name) VALUES (2, 'current account');
INSERT INTO tbl_account_types (id, name) VALUES (3, 'fixed term account');

INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (1, '1234222233334444', 800, 15, 30, 'CREATED', 1);
INSERT INTO tbl_accounts (id, account_number, balance, commission, movements_limit, state, account_type_id ) VALUES (2, '1111222233334444', 950, 30, 50, 'CREATED', 2);
--INSERT INTO tbl_accounts (id, account_number,account_type_id ) VALUES (1, '1234222233334444', 1);


INSERT INTO tbl_account_owner (id, customer_id, account_id) values (1,1,1);
INSERT INTO tbl_account_owner (id, customer_id, account_id) values (2,2,2);