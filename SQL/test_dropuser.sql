

INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('1@a.com','11111','aaaaa',TO_DATE('1986-04-16', 'YYYY-mm-dd'));
INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('2@a.com','22222','bbbbb',TO_DATE('1986-04-16', 'YYYY-mm-dd'));

INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('3@a.com','33333','ccccc',TO_DATE('1986-04-16', 'YYYY-mm-dd'));



INSERT INTO Message VALUES('1', '1@a.com', '2@a.com', TO_TIMESTAMP('2016-04-26 13:52:16.146','YYYY-MM-DD HH24:MI:SS:FF'),'convallis.in@perinceptos.net TO enim.consequat.purus@tinciduntnequevitae.org', 'convallis.in@perinceptos.net Hello --> enim.consequat.purus@tinciduntnequevitae.org');
INSERT INTO Message VALUES('1', '1@a.com', '3@a.com', TO_TIMESTAMP('2016-04-26 13:52:16.146','YYYY-MM-DD HH24:MI:SS:FF'),'convallis.in@perinceptos.net TO enim.consequat.purus@tinciduntnequevitae.org', 'convallis.in@perinceptos.net Hello --> enim.consequat.purus@tinciduntnequevitae.org');



DELETE FROM UserTable WHERE email= '1@a.com';
-- result should be , there are still 2 msgs in Message table

DELETE FROM UserTable WHERE email= '2@a.com';
-- only one msg left


 




