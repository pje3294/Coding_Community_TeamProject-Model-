-- ���̺� ��ȸ 
select * from all_tables;
select * from users; -- ȸ�����̺�
SELECT * FROM TEST; -- �ڵ� �Խù� ���̺�
SELECT * FROM TEST_REPLY; -- �ڵ� ���/���� ���̺� 

SELECT * FROM TEST_REPLY where user_num=1 and t_id=1 and parent_id=0;
-- -------------------------------------------------------------------------------------------------------
DROP TABLE USERS;
DROP TABLE TEST;
DROP TABLE TEST_REPLY;
-- -------------------------------------------------------------------------------------------------------
-- �� ���̺� ���� ��
-- ȸ�� ���̺� 
create table users(
user_num int primary key,
user_name varchar(15) not null,
user_id varchar(50) not null,
user_pw varchar(50) not null,
user_hp varchar(25) not null,
user_gender varchar(5) not null,
user_email varchar(225) not null,
user_addr varchar(225) not null,
user_birth varchar(30) not null,
icon_id varchar(30) not null
)

-- �ڵ� �׽�Ʈ ���̺�
create table test(
   t_id int primary key,
   user_num int not null,
   t_title varchar(100) not null,
   t_content varchar(4000) not null,
   t_answer varchar(4000) not null,
   T_EX VARCHAR(225) NOT NULL,
   t_writer varchar(50) not null,
   t_date date default sysdate,
   t_hit int default 0,
   t_lang varchar(20) not null,
   RE_CNT int default 0,
   constraint user_num_cons foreign key (user_num) references users(user_num) on delete cascade
)

-- �ڵ� �׽�Ʈ ��� ���̺�
CREATE TABLE TEST_REPLY(
	R_ID INT PRIMARY KEY, -- ��� ��ȣ
	T_ID INT NOT NULL, -- TEST�Խñ� ��ȣ
	USER_NUM INT NOT NULL, -- ȸ����ȣ
	R_CONTENT VARCHAR(225) NOT NULL, -- ���/���� ���� 
	R_DATE DATE DEFAULT SYSDATE, -- ���/���� �ۼ���
	DELETE_AT VARCHAR(1) DEFAULT 'N', -- ��� ���� ����
	R_WRITER VARCHAR(20) NOT NULL, -- �ۼ��� == ID
	PARENT_ID INT NOT NULL, -- ���� Ȯ��
	constraint t_id_cons foreign key (t_id) references test(t_id) on delete cascade,
	CONSTRAINT user_num_cons3 FOREIGN KEY (USER_NUM) REFERENCES users(user_num) on delete cascade
)
-- -------------------------------------------------------------------------------------------------------
-- -------------------------------------------------------------------------------------------------------
-- �� insert �� => �ʿ��Ͻø� �̿��ϼ���.
	-- USERS ���̺�
insert into USERS values((select NVL(MAX(user_num),0)+1 from users), '������', 'je','111','01011111111','F','je@gmail.com','�ּ�','19960927','1');
insert into USERS values((select NVL(MAX(user_num),0)+1 from users), 'GG', 'GG','111','01011111111','F','����@gmail.com','GG�ּ�','20210914','2');

	-- TEST ���̺�
INSERT INTO TEST (T_ID, USER_NUM, T_TITLE, T_CONTENT, T_ANSWER,T_EX, T_WRITER, T_LANG) VALUES ((select NVL(MAX(T_ID),0)+1 from TEST),3,'�ڵ�����1','��������1','��1','���1','je','JAVA');
INSERT INTO TEST (T_ID, USER_NUM, T_TITLE, T_CONTENT, T_ANSWER,T_EX, T_WRITER, T_LANG) VALUES ((select NVL(MAX(T_ID),0)+1 from TEST),4,'�ڵ�����2','����','��','���','j','JA');

-- TEST_REPLY ���̺�
INSERT INTO TEST_REPLY (R_ID, T_ID, USER_NUM, R_CONTENT, R_WRITER, PARENT_ID) VALUES ((select NVL(MAX(R_ID),0)+1 from TEST_REPLY),1,4,'���1','je',0);
INSERT INTO TEST_REPLY (R_ID, T_ID, USER_NUM, R_CONTENT, R_WRITER, PARENT_ID) VALUES ((select NVL(MAX(R_ID),0)+1 from TEST_REPLY),1,4,'���1_���1','je',0);

-- -------------------------------------------------------------------------------------------------------
-- -------------------------------------------------------------------------------------------------------
-- ���� ���� ���� 
UPDATE TEST_REPLY SET DELETE_AT='Y' WHERE R_ID =1 AND PARENT_ID=0 AND USER_NUM=1;
UPDATE TEST_REPLY SET DELETE_AT='Y' WHERE R_ID =13;

-- ������ ����Դϴ�. �������   ==> CASE WHEN X=Y THEN A ELSE B END : X=Y�� TRUE�� A�̰� �׷��� ������ B��.
SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'UNKNOWN' ELSE R_WRITER END, CASE WHEN DELETE_AT='Y' THEN '������ ���' ELSE R_CONTENT END, R_DATE, DELETE_AT, PARENT_ID FROM TEST_REPLY WHERE R_ID=1;

-- ���, ���� ���� 
UPDATE TEST_REPLY SET R_CONTENT='���1 ����' WHERE R_ID=1 ;

UPDATE TEST_REPLY SET DELETE_AT='Y' WHERE R_ID =1 AND PARENT_ID=0 AND USER_NUM=1;


SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'UNKNOWN' ELSE R_WRITER END, CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' ELSE R_CONTENT END, R_DATE, DELETE_AT, PARENT_ID FROM TEST_REPLY WHERE R_ID=?;

-- -------------------------------------------------------------------------------------------------------
-- -------------------------------------------------------------------------------------------------------

SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM TEST WHERE ROWNUM<=5 ORDER BY T_DATE DESC) WHERE 1<=RNUM AND T_title LIKE '%%';

SELECT COUNT(*) FROM TEST WHERE USER_NUM LIKE %1%;

-- ��ۼ�
SELECT * FROM TEST;
update TEST set RE_CNT= RE_CNT+8 where T_ID=18;

SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM TEST WHERE ROWNUM<=5 ORDER BY RE_CNT DESC) WHERE 1<=RNUM;

-- ���� ���
SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM TEST WHERE USER_NUM=1 AND ROWNUM<=5 ORDER BY T_HIT DESC) WHERE 1<=RNUM;


SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM TEST WHERE ROWNUM<=3 ORDER BY t_date DESC) WHERE 1<=RNUM;
SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM TEST WHERE ROWNUM<=3 ORDER BY max(T_DATE) DESC) WHERE 1<=RNUM;
																																							-- (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST WHERE USER_NUM=1 AND T_TITLE LIKE '%' ORDER BY T_DATE DESC) TEST WHERE ROWNUM <= 20) WHERE RNUM > 10 ORDER BY '%' DESC, T_DATE DESC;
-- -------------------------------------------------------------------------------------------------------
-- -------------------------------------------------------------------------------------------------------

-- �α���XXX (���/��񺸱�)
SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'UNKNOWN' ELSE R_WRITER END, CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' ELSE R_CONTENT END, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM,TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE PARENT_ID=0 AND T_ID=2 ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= 10) WHERE RNUM > 0 ORDER BY R_DATE DESC;


-- �α���OOO
SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'UNKNOWN' ELSE R_WRITER END, CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' ELSE R_CONTENT END, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM, TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE USER_NUM=1 AND PARENT_ID=0 AND T_ID=1 ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= 10) WHERE RNUM > 0 ORDER BY R_DATE DESC;


SELECT COUNT(*) FROM TEST_REPLY WHERE USER_NUM=4;

SELECT * FROM TEST_REPLY WHERE USER_NUM=4;

SELECT COUNT(*) FROM TEST_REPLY where T_ID=1;

-- SELECT * FROM TEST_REPLY WHERE not(PARENT_ID=0) ORDER BY R_DATE DESC;

SELECT COUNT(*) FROM TEST_REPLY WHERE delete_at='N' and t_id=1;

SELECT COUNT(*) FROM TEST_REPLY WHERE USER_NUM=1 AND DELETE_AT='N'



SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST WHERE T_TITLE LIKE '%%') TEST WHERE ROWNUM <= 10) WHERE RNUM > 0 ORDER BY T_HIT DESC;
SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST WHERE T_TITLE LIKE '%%') TEST WHERE ROWNUM <= 10) WHERE RNUM > 0 ORDER BY RE_CNT DESC;

















