package model.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.common.JNDI;
import model.users.UsersVO;

/* REPLY 테이블 컬럼명입니다.
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

	static int pageSize = 10; // 페이징 관련 변수

	static String sql_INSERT = "INSERT INTO TEST_REPLY (R_ID, T_ID, USER_NUM, R_CONTENT, R_WRITER, PARENT_ID) "
			+ "VALUES ((SELECT NVL(MAX(R_ID),0)+1 FROM TEST_REPLY),?,?,?,?,?)";

	static String sql_RECNT_UP = "UPDATE TEST SET RE_CNT= RE_CNT+1 WHERE T_ID=?";
	// 댓글/대댓글이 달리면 특정 TEST게시물의 댓글 수 (RE_CNT) ++

	static String sql_UPDATE = "UPDATE TEST_REPLY SET R_CONTENT=? WHERE R_ID =?"; // 댓글/대댓글 수정

//--------------------------------------------------------------------------------------------------------------------------		

	// getDBList 댓+대댓 = 1:N
	@SuppressWarnings("resource")
	public ArrayList<TestReplySet> getDBList(int pageNum, TestReplyVO vo) { // pageNum : 페이징관련 변수
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		String sql;
		int cnt = 0;

		ArrayList<TestReplySet> datas = new ArrayList<TestReplySet>();

		ArrayList<TestReplyVO> rrlist = null;
		TestReplyVO reply = null;

		try { // 전체 글

			// 전체 (로그인 안한 상태)

			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' "
					+ "ELSE R_WRITER END AS R_WRITER, CASE WHEN DELETE_AT='Y' THEN '*삭제된 댓글입니다.' "
					+ "ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM,"
					+ "TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE T_ID=? AND PARENT_ID=0 "
					+ "ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY R_DATE DESC";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.gettId());
			pstmt.setInt(2, (pageNum * pageSize) + pageSize);
			pstmt.setInt(3, pageNum * pageSize);
			// System.out.println("TestReplyVO로그인 X");

			ResultSet rs = pstmt.executeQuery(); // 댓글

			while (rs.next()) {

				reply = new TestReplyVO(); // 댓글
				// 댓글 다 넣어줌
				System.out.println("while문 입장");
				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				// System.out.println("reply 확인: " + rvo); // 로깅

				// 대댓글 유무에 따라서
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
					rrlist.add(rrvo); // 댓글 리스트
					// System.out.println("rrlist 확인: " + rrlist);

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
					System.out.println("cnt확인: " + cnt);
				}
				total.close();
				trs.setTestReCnt(cnt);

				datas.add(trs);

			}
			rs.close();

		} catch (Exception e) {
			System.out.println("ReplyDAO-selectDBList 오류");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		System.out.println("datas 확인: " + datas);
		return datas;

	}

/////////////////////////////////////////////////////////////////////////			
	// getDBList 댓+대댓 = 1:N
	@SuppressWarnings("resource")
	public TestMySet myReply(UsersVO uvo, int pageNum) { // pageNum : 페이징관련 변수
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		String sql;
		int cnt = 0;

		ArrayList<TestReplyVO> rlist = new ArrayList<TestReplyVO>();

		TestMySet datas = new TestMySet();

		try { // 로그인시

			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' "
					+ "ELSE R_WRITER END AS R_WRITER, CASE WHEN DELETE_AT='Y' THEN '*삭제된 댓글입니다.' "
					+ "ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, PARENT_ID FROM (SELECT ROWNUM AS RNUM, "
					+ "TEST_REPLY.* FROM (SELECT * FROM TEST_REPLY WHERE USER_NUM=? AND DELETE_AT='N' "
					+ "ORDER BY R_DATE DESC) TEST_REPLY WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY R_DATE DESC";
			// AND T_ID=? "
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uvo.getUserNum());
			System.out.println("getUserNum: " + uvo.getUserNum());
			pstmt.setInt(2, (pageNum * pageSize) + pageSize);
			pstmt.setInt(3, pageNum * pageSize);
			System.out.println("TestReplyVO로그인 O");

			ResultSet rs = pstmt.executeQuery(); // 댓글

			while (rs.next()) {
				// TestReplySet ts = new TestReplySet();
				TestReplyVO reply = new TestReplyVO(); // 댓글
				System.out.println("while문 입장");
				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				rlist.add(reply);
				// System.out.println("reply 확인: " + rvo); // 로깅

			}
			rs.close();

			// System.out.println("로그인 한 댓글 갯수");
			sql = "SELECT COUNT(*) FROM TEST_REPLY WHERE USER_NUM=? AND DELETE_AT='N'";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uvo.getUserNum());

			ResultSet total = pstmt.executeQuery();
			if (total.next()) {
				cnt = total.getInt(1);
				System.out.println("cnt확인: " + cnt);
			}
			total.close();

			// reply.setTestRecnt(cnt);
			datas.setRlist(rlist);
			datas.setTestRecnt(cnt);

		} catch (Exception e) {
			System.out.println("ReplyDAO-selectDBList 오류");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		System.out.println("datas 확인: " + datas);
		return datas;

	}

/////////////////////////////////////////////////////////////////////////			

	// getDBData
	@SuppressWarnings("resource")
	public TestReplySet getDBData(TestReplyVO vo) { // testSet으로 리턴
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		TestReplySet data = null;

		TestReplyVO reply = null;

		String sql;

		try {
			sql = "SELECT R_ID, T_ID, USER_NUM, CASE WHEN DELETE_AT='Y' THEN 'unknown' ELSE R_WRITER END AS R_WRITER, "
					+ "CASE WHEN DELETE_AT='Y' THEN '*삭제된 댓글입니다.' ELSE R_CONTENT END AS R_CONTENT, R_DATE, DELETE_AT, "
					+ "PARENT_ID FROM TEST_REPLY WHERE R_ID=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getrId());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				reply = new TestReplyVO(); // 댓글

				reply.setrId(rs.getInt("R_ID"));
				reply.settId(rs.getInt("T_ID"));
				reply.setUserNum(rs.getInt("USER_NUM"));
				reply.setrContent(rs.getString("R_CONTENT"));
				reply.setrDate(rs.getDate("R_DATE"));
				reply.setDeleteAt(rs.getString("DELETE_AT"));
				reply.setrWriter(rs.getString("R_WRITER"));
				reply.setParentId(rs.getInt("PARENT_ID"));

				rs.close();

				System.out.println("Testreply 확인: " + reply); // 로깅

				ArrayList<TestReplyVO> rrlist = new ArrayList<TestReplyVO>();

				// 대댓글 유무에 따라서
				if (reply.getParentId() > 0) { // 대댓글 존재한다면
					sql = "SELECT * FROM TEST_REPLY WHERE PARENT_ID=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, reply.getrId()); // praent_id == 댓글의 rid

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
						rrlist.add(rrvo); // 대댓글 리스트

						System.out.println("rrlist 확인: " + rrlist);
					}
					rrs.close();
				}

				data.setReply(reply);
				data.setRrlist(rrlist);
			}

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-getDBData 오류로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return data;

	}

/////////////////////////////////////////////////////////////////////////			

	// insert --> 댓글 수(RE_CNT) update 트랜잭션!!!!!!★
	@SuppressWarnings("resource")
	public boolean insert(TestReplyVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		boolean check = false; // 트랜잭션 커밋, 롤백 여부 판단 변수

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
			System.out.println("TestReplyDAO-insert 오류 로깅");
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

		boolean check = false; // 트랜잭션 커밋, 롤백 여부 판단 변수

		// ** 댓글 삭제인 경우 --> 진짜 삭제가 아닌 ,,, UPDATE 개념 (DELETE_AT = 'N' --> 'Y')
		String sql_DELETE_R1 = "DELETE FROM TEST_REPLY WHERE R_ID=?";
		String sql_DELETE_R2 = "UPDATE TEST_REPLY SET DELETE_AT='Y' WHERE R_ID =? AND PARENT_ID=0";

		// ** 대댓글 삭제인 경우 --> 진짜 삭제
		String sql_DELETE_RR = "DELETE FROM TEST_REPLY WHERE R_ID =? AND PARENT_ID=?";

		// 댓글달린 특정 TEST게시물의 댓글 수 (RE_CNT) --
		String sql_RECNT_DN = "UPDATE TEST SET RE_CNT= RE_CNT-1 WHERE T_ID=?";

		String sql_COUNT = "SELECT COUNT(*) FROM TEST_REPLY WHERE PARENT_ID=?";

		int cnt = 0;

		try {
			if (vo.getParentId() == 0) {

				// 대댓글이 달린 애인지, 대댓글 없는 댓글인지 확인

				conn.setAutoCommit(false);

				pstmt = conn.prepareStatement(sql_COUNT);
				pstmt.setInt(1, vo.getrId());
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					cnt = rs.getInt(1);
					System.out.println(cnt);
				}
				if (cnt == 0) { // 대댓글 없는 댓글 삭제 시, -> 완전 삭제
					pstmt = conn.prepareStatement(sql_DELETE_R1); // 완전 DB에서 삭제
					pstmt.setInt(1, vo.getrId());
					pstmt.executeUpdate();
					System.out.println("대댓글 없는 댓글 삭제 완료 로깅");

				} else { // 대댓글이 있는 댓글 삭제 -> DELETE_AT을 "N -> Y"로 변경
					pstmt = conn.prepareStatement(sql_DELETE_R2);
					pstmt.setInt(1, vo.getrId());
					pstmt.executeUpdate();
					System.out.println("대댓글 있는 댓글 삭제 완료 로깅");

				}

			} else { // 삭제 대상이 "대댓글"이라면,
				pstmt = conn.prepareStatement(sql_DELETE_RR);
				pstmt.setInt(1, vo.getrId());
				pstmt.executeUpdate();
			}

			pstmt = conn.prepareStatement(sql_RECNT_DN); // 해당 게시물 댓글수 -1하기 => 댓글, 대댓 삭제 시 게시글 댓글 수 --
			pstmt.setInt(1, vo.gettId());
			pstmt.executeUpdate();

			check = true; // 커밋 하기위해

			if (check) {
				conn.commit();
				// System.out.println("커밋확인");
				res = true;
			} else {
				// System.out.println("롤백확인");
				conn.rollback();
			}

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-delete 오류 로깅");
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

			System.out.println("댓글/대댓글 수정 확인"); // 로깅

			res = true;

		} catch (SQLException e) {
			System.out.println("TestReplyDAO-update 오류 로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}

/////////////////////////////////////////////////////////////////////////	

}
