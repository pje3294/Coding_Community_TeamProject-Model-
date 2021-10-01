package model.test;

import java.util.ArrayList;

public class TestReplySet { // ´ñ +´ë´ñ
	private int testReCnt; //´ë´ñ±Û ÀüÃ¼ °¹¼ö 
	private TestReplyVO reply; // ´ñ±Û 1:
	private ArrayList<TestReplyVO> rrlist = new ArrayList<TestReplyVO>(); // ´ë´ñ±Û N

	public TestReplyVO getReply() {
		return reply;
	}

	public void setReply(TestReplyVO reply) {
		this.reply = reply;
	}

	public ArrayList<TestReplyVO> getRrlist() {
		return rrlist;
	}

	public void setRrlist(ArrayList<TestReplyVO> rrlist) {
		this.rrlist = rrlist;
	}

	public int getTestReCnt() {
		return testReCnt;
	}

	public void setTestReCnt(int testReCnt) {
		this.testReCnt = testReCnt;
	}

	@Override
	public String toString() {
		return "TestReplySet [testReCnt=" + testReCnt + ", reply=" + reply + ", rrlist=" + rrlist + "]";
	}

}
