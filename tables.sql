drop table UserTable cascade constraints;
create table UserTable (
	userID number(10) not null,
	name varchar2(64) not null,
	email varchar2(128) not null,
	birthday date,
	constraints pk_users primary key(userID),
	CONSTRAINT uc_email UNIQUE (email)
);


drop table Friendship cascade constraints;
create table Friendship (
	person1 number(10) not null,
	person2 number(10) not null,
	timeInitiated timestamp not null,
	timeEstablished timestamp,
	constraints pk_friendship primary key(person1, person2),
	constraints fk_person1 foreign key(person1) references UserTable(userID),
	constraints fk_person2 foreign key(person2) references UserTable(userID)
);


drop table GroupTable cascade constraints;
create table GroupTable (
	groupID number(10) not null,
	name varchar2(64) not null,
	description varchar2(1024),
	mLimit number not null,
	constraints pk_groups primary key(groupID)
);

drop table Membership cascade constraints;
create table Membership (
	groupID number(10) not null,
	userID number(10) not null,
	constraints pk_membership primary key(groupID, userID),
	constraints fk_m_group foreign key(groupID) references GroupTable(groupID),
	constraints fk_m_user foreign key(userID) references UserTable(userID)
);

drop table Message cascade constraints;
create table Message (
	msgID number(10) not null,
	senderID number(10) not null,
	recipientID number(10) not null,
	time_sent timestamp,
	msg_subject varchar2(1024),
	msg_body varchar2(1024),
	constraints pk_message primary key(msgID),
	constraints fk_sender foreign key(senderID) references UserTable(userID),
	constraints fk_recipient foreign key(recipientID) references UserTable(userID)
);





