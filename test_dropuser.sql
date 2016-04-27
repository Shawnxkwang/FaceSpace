drop table UserTable cascade constraints;
-- UserTable(email, firstName, lastName, birthday)
create table UserTable (
	-- userID number(10) not null,
	email varchar2(128) not null,
	firstName varchar2(64) not null,
	lastName varchar2(64) not null,
	birthday date,
	lastLoginTime timestamp,
	constraints pk_users primary key(email)
);


drop table Message cascade constraints;
-- Message(msgID, senderEmail, recipientEmail, time_sent, msg_subject, msg_body)
create table Message (
	msgID number(10) not null,
	senderEmail varchar2(128) not null,
	recipientEmail varchar2(128) not null,
	time_sent timestamp,
	msg_subject varchar2(1024),
	msg_body varchar2(1024),
	constraints pk_message primary key(msgID)
	--constraints fk_sender foreign key(senderEmail) references UserTable(email),
	--constraints fk_recipient foreign key(recipientEmail) references UserTable(email)
);

-- auto generate megID
DROP SEQUENCE msg_seq;
Create sequence msg_seq start with 1
increment by 1
minvalue 1
maxvalue 10000;
CREATE OR REPLACE TRIGGER make_msg_id
BEFORE INSERT ON Message
FOR EACH ROW
BEGIN
  SELECT msg_seq.NEXTVAL
  INTO   :new.msgID
  FROM   dual;
END;
/


-- A message is deleted only when both the sender and all receivers are deleted
CREATE OR REPLACE TRIGGER del_msg_when_drop_user
AFTER DELETE ON UserTable
BEGIN
	DELETE FROM Message
		WHERE 	(senderEmail NOT IN (SELECT email FROM UserTable))
			AND ( recipientEmail NOT IN (SELECT email FROM UserTable) );
END;
/


INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('1@a.com','11111','aaaaa',TO_DATE('1986-04-16', 'YYYY-mm-dd'));
INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('2@a.com','22222','bbbbb',TO_DATE('1986-04-16', 'YYYY-mm-dd'));

INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('3@a.com','33333','ccccc',TO_DATE('1986-04-16', 'YYYY-mm-dd'));



INSERT INTO Message VALUES('1', '1@a.com', '2@a.com', TO_TIMESTAMP('2016-04-26 13:52:16.146','YYYY-MM-DD HH24:MI:SS:FF'),'convallis.in@perinceptos.net TO enim.consequat.purus@tinciduntnequevitae.org', 'convallis.in@perinceptos.net Hello --> enim.consequat.purus@tinciduntnequevitae.org');
INSERT INTO Message VALUES('1', '1@a.com', '3@a.com', TO_TIMESTAMP('2016-04-26 13:52:16.146','YYYY-MM-DD HH24:MI:SS:FF'),'convallis.in@perinceptos.net TO enim.consequat.purus@tinciduntnequevitae.org', 'convallis.in@perinceptos.net Hello --> enim.consequat.purus@tinciduntnequevitae.org');



DELETE FROM UserTable WHERE email= '1@a.com';
-- result should be , there are still 2 msgs in Message table

DELETE FROM UserTable WHERE email= '2@a.com';
-- only one msg left


 




