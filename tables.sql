drop table UserTable cascade constraints;
create table UserTable (
	-- userID number(10) not null,
	email varchar2(128) not null,
	firstName varchar2(64) not null,
	lastName varchar2(64) not null,
	birthday date,
	constraints pk_users primary key(email)
);


drop table Friendship cascade constraints;
create table Friendship (
	person1 varchar2(128) not null,
	person2 varchar2(128) not null,
	timeInitiated timestamp not null,
	timeEstablished timestamp,
	constraints pk_friendship primary key(person1, person2),
	constraints fk_person1 foreign key(person1) references UserTable(email),
	constraints fk_person2 foreign key(person2) references UserTable(email)
);


-- Add creator column for adding creator to group purposes?
-- Add current Number of members?

drop table GroupTable cascade constraints;
create table GroupTable (
	groupID number(10) not null,
	name varchar2(64) not null,
	description varchar2(1024),
	mLimit number not null,
	constraints pk_groups primary key(groupID)
);

Create sequence group_seq start with 1
increment by 1
minvalue 1
maxvalue 1000000;

CREATE OR REPLACE TRIGGER make_group_id
BEFORE INSERT ON GroupTable
FOR EACH ROW
BEGIN
  SELECT group_seq.NEXTVAL
  INTO   :new.groupID
  FROM   dual;
END;
/


drop table Membership cascade constraints;
create table Membership (
	groupID number(10) not null,
	member varchar2(128) not null,
	constraints pk_membership primary key(groupID, member),
	constraints fk_m_group foreign key(groupID) references GroupTable(groupID),
	constraints fk_m_user foreign key(member) references UserTable(email)
);

drop table Message cascade constraints;
create table Message (
	msgID number(10) not null,
	senderEmail varchar2(128) not null,
	recipientEmail varchar2(128) not null,
	time_sent timestamp,
	msg_subject varchar2(1024),
	msg_body varchar2(1024),
	constraints pk_message primary key(msgID),
	constraints fk_sender foreign key(senderEmail) references UserTable(email),
	constraints fk_recipient foreign key(recipientEmail) references UserTable(email)
);

Create sequence message_seq start with 1
increment by 1
minvalue 1
maxvalue 10000000;

CREATE OR REPLACE TRIGGER make_group_id
BEFORE INSERT ON GroupTable
FOR EACH ROW
BEGIN
  SELECT message_seq.NEXTVAL
  INTO   :new.msgID
  FROM   dual;
END;
/




