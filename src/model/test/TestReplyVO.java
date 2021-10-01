package model.test;

import java.util.Date;

/*
 * 	R_ID INT PRIMARY KEY,
	T_ID INT NOT NULL,
	USER_NUM INT NOT NULL,
	R_CONTENT VARCHAR(225) NOT NULL,
	R_DATE DATE DEFAULT SYSDATE,
	DELETE_AT VARCHAR(1) DEFAULT 'N',
	R_WRITER VARCHAR(20) NOT NULL,
	PARENT_ID INT NOT NULL,
	constraint t_id_cons foreign key (t_id) references test(t_id) on delete cascade,
	CONSTRAINT user_num_cons3 FOREIGN KEY (USER_NUM) REFERENCES users(user_num) on delete cascade
 * */

public class TestReplyVO { // Å×½ºÆ® ´ñ±Û

	private int rId;
	private int tId;
	private int userNum;
	private String rContent;
	private Date rDate;
	private String deleteAt;
	private String rWriter;
	private int parentId;

	public int getrId() {
		return rId;
	}

	public void setrId(int rId) {
		this.rId = rId;
	}

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

	public String getrContent() {
		return rContent;
	}

	public void setrContent(String rContent) {
		this.rContent = rContent;
	}

	public Date getrDate() {
		return rDate;
	}

	public void setrDate(Date rDate) {
		this.rDate = rDate;
	}

	public String getDeleteAt() {
		return deleteAt;
	}

	public void setDeleteAt(String deleteAt) {
		this.deleteAt = deleteAt;
	}

	public String getrWriter() {
		return rWriter;
	}

	public void setrWriter(String rWriter) {
		this.rWriter = rWriter;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "TestReplyVO [rId=" + rId + ", tId=" + tId + ", userNum=" + userNum + ", rContent=" + rContent
				+ ", rDate=" + rDate + ", deleteAt=" + deleteAt + ", rWriter=" + rWriter + ", parentId=" + parentId
				+ "]";
	}

	

}
