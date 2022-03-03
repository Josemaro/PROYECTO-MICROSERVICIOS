INSERT INTO tbl_credits (id, category,credit_code,interest_rate,money_amount,number_of_instalments,customer_id,create_at)
 VALUES (1, 1,'111',0.2,2000,10,1,'2022-03-02');
INSERT INTO tbl_credits (id, category,credit_code,interest_rate,money_amount,number_of_instalments,customer_id,create_at)
 VALUES (2, 2,'112',0.15,35000,30,5,'2022-03-02');


INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (1,1,400,'PAID','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (2,1,400,'PAID','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (3,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (4,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (5,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (6,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (7,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (8,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (9,1,400,'PENDING','2022-03-02');
INSERT INTO tbl_payments (id,credit_id,amount,state,create_at) values (10,1,400,'PENDING','2022-03-02');

