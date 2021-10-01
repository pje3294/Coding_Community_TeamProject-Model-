package model.test;

import java.util.ArrayList;

public class TestSet {
	private int testCnt; // test게시글  전체 갯수
	private ArrayList<TestVO> tlist = new ArrayList<TestVO>();

	public int getTestCnt() {
		return testCnt;
	}

	public void setTestCnt(int testCnt) {
		this.testCnt = testCnt;
	}

	public ArrayList<TestVO> getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList<TestVO> tlist) {
		this.tlist = tlist;
	}

	@Override
	public String toString() {
		return "TestSet [testCnt=" + testCnt + ", tlist=" + tlist + "]";
	}

}
