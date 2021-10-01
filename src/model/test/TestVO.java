package model.test;

import java.util.Date;

/*
 *      t_id int primary key,
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
 * */

public class TestVO { //Å×½ºÆ® VO
	private int tId;
	private int userNum;
	private String tTitle;
	private String tContent;
	private String tAnswer;
	private String tEx;
	private String tWriter;
	private Date tDate;
	private int tHit;
	private String tLang;
	private int reCnt;

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public String gettTitle() {
		return tTitle;
	}

	public void settTitle(String tTitle) {
		this.tTitle = tTitle;
	}

	public String gettContent() {
		return tContent;
	}

	public void settContent(String tContent) {
		this.tContent = tContent;
	}

	public String gettAnswer() {
		return tAnswer;
	}

	public void settAnswer(String tAnswer) {
		this.tAnswer = tAnswer;
	}

	public String gettEx() {
		return tEx;
	}

	public void settEx(String tEx) {
		this.tEx = tEx;
	}

	public String gettWriter() {
		return tWriter;
	}

	public void settWriter(String tWriter) {
		this.tWriter = tWriter;
	}

	public Date gettDate() {
		return tDate;
	}

	public void settDate(Date tDate) {
		this.tDate = tDate;
	}

	public int gettHit() {
		return tHit;
	}

	public void settHit(int tHit) {
		this.tHit = tHit;
	}

	public String gettLang() {
		return tLang;
	}

	public void settLang(String tLang) {
		this.tLang = tLang;
	}

	public int getReCnt() {
		return reCnt;
	}

	public void setReCnt(int reCnt) {
		this.reCnt = reCnt;
	}

	@Override
	public String toString() {
		return "   TestVO [tId=" + tId + ", userNum=" + userNum + ", tTitle=" + tTitle + ", tContent=" + tContent
				+ ", tAnswer=" + tAnswer + ", tEx=" + tEx + ", tWriter=" + tWriter + ", tDate=" + tDate + ", tHit="
				+ tHit + ", tLang=" + tLang + ", reCnt=" + reCnt + "]";
	}

}
