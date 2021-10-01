package model.test;

import java.util.ArrayList;

public class TestMySet {
	private int testRecnt; // 마이페이지의 나의 댓글,대댓글 보여주기위한 전체 총 갯수
	private ArrayList<TestReplyVO> rlist = new ArrayList<TestReplyVO>();

	public int getTestRecnt() {
		return testRecnt;
	}

	public void setTestRecnt(int testRecnt) {
		this.testRecnt = testRecnt;
	}

	public ArrayList<TestReplyVO> getRlist() {
		return rlist;
	}

	public void setRlist(ArrayList<TestReplyVO> rlist) {
		this.rlist = rlist;
	}

	@Override
	public String toString() {
		return "TestMySet [testRecnt=" + testRecnt + ", rlist=" + rlist + "]";
	}



	
	
	
}
