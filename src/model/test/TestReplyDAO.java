package model.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.common.JNDI;
import model.users.UsersVO;

/* REPLY ���̺� �÷����Դϴ�.
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

public class TestReplyDAO {

	static int pageSize = 10; // ����¡ ���� ����

	static String sql_INSERT = "INSERT INTO TEST_REPLY (R_ID, T_ID, USER_NUM, R_CONTENT, R_WRITER, PARENT_ID) "
			+ "VALUES ((SELECT NVL(MAX(R_ID),0)+1 FROM TEST_REPLY),?,?,?,?,?)";

	static String sql_RECNT_UP = "UPDATE TEST SET RE_CNT= RE_CNT+1 WHERE T_ID=?";
	// ���/������ �޸��� Ư�� TEST�Խù��� ��� �� (RE_CNT) ++

	static String sql_UPDATE = "UPDATE TEST_REPLY SET R_CONTENT=? WHERE R_ID =?"; // ���/���� ����

//--------------------------------------------------------------------------------------------------------------------------		

	// getDBList ��+��� = 1:N
	@SuppressWarnings("resource")
	public ArrayList<TestReplySet> getDBList(int pageNum, TestReplyVO vo) { // pageNum : ����¡���� ����
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		String sql;
		int cnt = 0;

		ArrayList<TestReplySet> datas = new ArrayList<TestReplySet>();

		ArrayList<TestReplyVO> rrlist = null;
		TestReplyVO reply = null;

		try { // ��ü ��

			// ��ü (�α��� ���� ����)

			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' "
					+ "ELSE R_WRITER END AS R_WRITER, CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' "
					+ "ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM,"
					+ "TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE T_ID=? AND PARENT_ID=0 "
					+ "ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY R_DATE DESC";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.gettId());
			pstmt.setInt(2, (pageNum * pageSize) + pageSize);
			pstmt.setInt(3, pageNum * pageSize);
			// System.out.println("TestReplyVO�α��� X");

			ResultSet rs = pstmt.executeQuery(); // ���

			while (rs.next()) {

				reply = new TestReplyVO(); // ���
				// ��� �� �־���
				System.out.println("while�� ����");
				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				// System.out.println("reply Ȯ��: " + rvo); // �α�

				// ���� ������ ����
				sql = "SELECT * FROM TEST_REPLY WHERE PARENT_ID=? ORDER BY R_DATE DESC";

				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, reply.getrId());

				ResultSet rrs = pstmt.executeQuery();

				rrlist = new ArrayList<TestReplyVO>();

				while (rrs.next()) {
					TestReplyVO rrvo = new TestReplyVO();

					rrvo.setrId(rrs.getInt("R_ID"));
					rrvo.settId(rrs.getInt("T_ID"));
					rrvo.setUserNum(rrs.getInt("USER_NUM"));
					rrvo.setrContent(rrs.getString("R_CONTENT"));
					rrvo.setrDate(rrs.getDate("R_DATE"));
					rrvo.setDeleteAt(rrs.getString("DELETE_AT"));
					rrvo.setrWriter(rrs.getString("R_WRITER"));
					rrvo.setParentId(rrs.getInt("PARENT_ID"));
					rrlist.add(rrvo); // ��� ����Ʈ
					// System.out.println("rrlist Ȯ��: " + rrlist);

				}
				rrs.close();

				TestReplySet trs = new TestReplySet();
				trs.setReply(reply);
				trs.setRrlist(rrlist);

				sql = "SELECT COUNT(*) FROM TEST_REPLY where T_ID=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, vo.gettId());

				ResultSet total = pstmt.executeQuery();
				if (total.next()) {
					cnt = total.getInt(1);
					System.out.println("cntȮ��: " + cnt);
				}
				total.close();
				trs.setTestReCnt(cnt);

				datas.add(trs);

			}
			rs.close();

		} catch (Exception e) {
			System.out.println("ReplyDAO-selectDBList ����");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		System.out.println("datas Ȯ��: " + datas);
		return datas;

	}

/////////////////////////////////////////////////////////////////////////			
	// getDBList ��+��� = 1:N
	@SuppressWarnings("resource")
	public TestMySet myReply(UsersVO uvo, int pageNum) { // pageNum : ����¡���� ����
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		String sql;
		int cnt = 0;

		ArrayList<TestReplyVO> rlist = new ArrayList<TestReplyVO>();

		TestMySet datas = new TestMySet();

		try { // �α��ν�

			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' "
					+ "ELSE R_WRITER END AS R_WRITER, CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' "
					+ "ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM, "
					+ "TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE USER_NUM=? AND DELETE_AT='N' "
					+ "ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY R_DATE DESC";
			// AND T_ID=? "
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uvo.getUserNum());
			System.out.println("getUserNum: " + uvo.getUserNum());
			pstmt.setInt(2, (pageNum * pageSize) + pageSize);
			pstmt.setInt(3, pageNum * pageSize);
			System.out.println("TestReplyVO�α��� O");

			ResultSet rs = pstmt.executeQuery(); // ���

			while (rs.next()) {
				// TestReplySet ts = new TestReplySet();
				TestReplyVO reply = new TestReplyVO(); // ���
				System.out.println("while�� ����");
				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				rlist.add(reply);
				// System.out.println("reply Ȯ��: " + rvo); // �α�

			}
			rs.close();

			// System.out.println("�α��� �� ��� ����");
			sql = "SELECT COUNT(*) FROM TEST_REPLY WHERE USER_NUM=? AND DELETE_AT='N'";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uvo.getUserNum());

			ResultSet total = pstmt.executeQuery();
			if (total.next()) {
				cnt = total.getInt(1);
				System.out.println("cntȮ��: " + cnt);
			}
			total.close();

			// reply.setTestRecnt(cnt);
			datas.setRlist(rlist);
			datas.setTestRecnt(cnt);

		} catch (Exception e) {
			System.out.println("ReplyDAO-selectDBList ����");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		System.out.println("datas Ȯ��: " + datas);
		return datas;

	}

/////////////////////////////////////////////////////////////////////////			

	// getDBData
	@SuppressWarnings("resource")
	public TestReplySet getDBData(TestReplyVO vo) { // testSet���� ����
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		TestReplySet data = null;

		TestReplyVO reply = null;

		String sql;

		try {
			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' ELSE R_WRITER END AS R_WRITER, "
					+ "CASE WHEN DELETE_AT='Y' THEN '*������ ����Դϴ�.' ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, "
					+ "PARENT_ID FROM TEST_REPLY WHERE R_ID=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getrId());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				reply = new TestReplyVO(); // ���

				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				rs.close();

				System.out.println("Testreply Ȯ��: " + reply); // �α�

				ArrayList<TestReplyVO> rrlist = new ArrayList<TestReplyVO>();

				// ���� ������ ����
				if (reply.getParentId() > 0) { // ���� �����Ѵٸ�
					sql = "SELECT * FROM TEST_REPLY WHERE PARENT_ID=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, reply.getrId()); // praent_id == ����� rid

					ResultSet rrs = pstmt.executeQuery();

					while (rrs.next()) {
						TestReplyVO rrvo = new TestReplyVO();

						rrvo.setrId(rrs.getInt("R_ID"));
						rrvo.settId(rrs.getInt("T_ID"));
						rrvo.setUserNum(rrs.getInt("USER_NUM"));
						rrvo.setrContent(rrs.getString("R_CONTENT"));
						rrvo.setrDate(rrs.getDate("R_DATE"));
						rrvo.setDeleteAt(rrs.getString("DELETE_AT"));
						rrvo.setrWriter(rrs.getString("R_WRITER"));
						rrvo.setParentId(rrs.getInt("PARENT_ID"));
						rrlist.add(rrvo); // ���� ����Ʈ

						System.out.println("rrlist Ȯ��: " + rrlist);
					}
					rrs.close();
				}

				data.setReply(reply);
				data.setRrlist(rrlist);
			}

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-getDBData �����α�");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return data;

	}

/////////////////////////////////////////////////////////////////////////			

	// insert --> ��� ��(RE_CNT) update Ʈ�����!!!!!!��
	@SuppressWarnings("resource")
	public boolean insert(TestReplyVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		boolean check = false; // Ʈ����� Ŀ��, �ѹ� ���� �Ǵ� ����

		try {
			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sql_INSERT);

			pstmt.setInt(1, vo.gettId());
			pstmt.setInt(2, vo.getUserNum());
			pstmt.setString(3, vo.getrContent());
			pstmt.setString(4, vo.getrWriter());
			pstmt.setInt(5, vo.getParentId());
			pstmt.executeUpdate();

			pstmt = conn.prepareStatement(sql_RECNT_UP);
			pstmt.setInt(1, vo.gettId());
			pstmt.executeUpdate();
			check = true;

			if (check) {
				conn.commit();
				res = true;
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-insert ���� �α�");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}

/////////////////////////////////////////////////////////////////////////

// delete 
	@SuppressWarnings("resource")
	public boolean delete(TestReplyVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		boolean check = false; // Ʈ����� Ŀ��, �ѹ� ���� �Ǵ� ����

		// ** ��� ������ ��� --> ��¥ ������ �ƴ� ,,, UPDATE ���� (DELETE_AT = 'N' --> 'Y')
		String sql_DELETE_R1 = "DELETE FROM TEST_REPLY WHERE R_ID=?";
		String sql_DELETE_R2 = "UPDATE TEST_REPLY SET DELETE_AT='Y' WHERE R_ID =? AND PARENT_ID=0";

		// ** ���� ������ ��� --> ��¥ ����
		String sql_DELETE_RR = "DELETE FROM TEST_REPLY WHERE R_ID =? AND PARENT_ID=?";

		// ��۴޸� Ư�� TEST�Խù��� ��� �� (RE_CNT) --
		String sql_RECNT_DN = "UPDATE TEST SET RE_CNT= RE_CNT-1 WHERE T_ID=?";

		String sql_COUNT = "SELECT COUNT(*) FROM TEST_REPLY WHERE PARENT_ID=?";

		int cnt = 0;

		try {
			if (vo.getParentId() == 0) {

				// ������ �޸� ������, ���� ���� ������� Ȯ��

				conn.setAutoCommit(false);

				pstmt = conn.prepareStatement(sql_COUNT);
				pstmt.setInt(1, vo.getrId());
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					cnt = rs.getInt(1);
					System.out.println(cnt);
				}
				if (cnt == 0) { // ���� ���� ��� ���� ��, -> ���� ����
					pstmt = conn.prepareStatement(sql_DELETE_R1); // ���� DB���� ����
					pstmt.setInt(1, vo.getrId());
					pstmt.executeUpdate();
					System.out.println("���� ���� ��� ���� �Ϸ� �α�");

				} else { // ������ �ִ� ��� ���� -> DELETE_AT�� "N -> Y"�� ����
					pstmt = conn.prepareStatement(sql_DELETE_R2);
					pstmt.setInt(1, vo.getrId());
					pstmt.executeUpdate();
					System.out.println("���� �ִ� ��� ���� �Ϸ� �α�");

				}

			} else { // ���� ����� "����"�̶��,
				pstmt = conn.prepareStatement(sql_DELETE_RR);
				pstmt.setInt(1, vo.getrId());
				pstmt.executeUpdate();
			}

			pstmt = conn.prepareStatement(sql_RECNT_DN); // �ش� �Խù� ��ۼ� -1�ϱ� => ���, ��� ���� �� �Խñ� ��� �� --
			pstmt.setInt(1, vo.gettId());
			pstmt.executeUpdate();

			check = true; // Ŀ�� �ϱ�����

			if (check) {
				conn.commit();
				// System.out.println("Ŀ��Ȯ��");
				res = true;
			} else {
				// System.out.println("�ѹ�Ȯ��");
				conn.rollback();
			}

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-delete ���� �α�");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;

	}

/////////////////////////////////////////////////////////////////////////	

	// update
	public boolean update(TestReplyVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql_UPDATE);
			pstmt.setString(1, vo.getrContent());
			pstmt.setInt(2, vo.getrId());
			pstmt.executeUpdate();

			System.out.println("���/���� ���� Ȯ��"); // �α�

			res = true;

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-update ���� �α�");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}

/////////////////////////////////////////////////////////////////////////	

}
