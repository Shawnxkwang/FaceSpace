
drop table GroupTable cascade constraints;
create table GroupTable (
	groupID number(10) not null,
	name varchar2(64) not null,
	description varchar2(1024),
	mLimit number not null,
	constraints pk_groups primary key(groupID)
);

CREATE OR REPLACE  sequence group_seq start with 1
increment by 1
minvalue 1
maxvalue 10000;

CREATE OR REPLACE TRIGGER make_group_id
BEFORE INSERT ON GroupTable
FOR EACH ROW
BEGIN
  SELECT group_seq.NEXTVAL
  INTO   :new.groupID
  FROM   dual;
END;
/


INSERT INTO GroupTable VALUES(1, 'group_name', 'g_desc', 100);
INSERT INTO GroupTable VALUES(1, 'group_name', 'g_desc', 100);

SELECT * FROM GroupTable;

