package model.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.catalina.core.JniLifecycleListener;

import model.common.JNDI;
import model.users.UsersVO;

/* TEST 테이블 컬럼명입니다.
 *   t_id int primary key,
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

public class TestDAO { // sql문 모두 상단배치???

	static int pageSize = 10; // 페이징 관련 변수

	static String sql_INSERT = "INSERT INTO TEST (T_ID, USER_NUM, T_TITLE, T_CONTENT, T_ANSWER, T_EX, T_WRITER, T_LANG) "
			+ "VALUES ((SELECT NVL(MAX(T_ID),0)+1 FROM TEST),?,?,?,?,?,?,?)";
	static String sql_SELECT_ONE = "SELECT * FROM TEST WHERE T_ID=?";
	static String sql_HIT_UP = "UPDATE TEST SET T_HIT=T_HIT+1 WHERE T_ID=?"; // 게시글 조회 == 조회수 ++
	static String sql_UPDATE = "UPDATE TEST SET T_TITLE=?, T_CONTENT=?, T_ANSWER=?, T_EX=?, T_LANG=? WHERE T_ID=?";
	static String sql_DELETE = "DELETE FROM TEST WHERE T_ID =?";

//--------------------------------------------------------------------------------------------------------------------------	

	// getDBList --> 검색기능까지 + 정렬(최신, 댓글, 조회순)
	@SuppressWarnings("resource")
	public TestSet getDBList(String content, int pageNum, UsersVO uvo, String order) {
		Connection conn = JNDI.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs;
		String sql;
		int cnt = 0;

		TestSet datas = new TestSet();
		TestVO data = null;

		try { // 로그인 한경우
			if (uvo.getUserNum() > 0) {
				if (order.equals("댓글순")) { // RE_CNT
					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST "
							+ "WHERE USER_NUM=? AND T_TITLE LIKE '%" + content + "%' ORDER BY RE_CNT DESC, T_DATE DESC) "
							+ "TEST WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY RE_CNT DESC";

				} else if (order.equals("조회순")) { // T_HIT
					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST "
							+ "WHERE USER_NUM=? AND T_TITLE LIKE '%" + content + "%' ORDER BY T_HIT DESC, T_DATE DESC) "
							+ "TEST WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY T_HIT DESC";
				} else {

					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM ("
							+ "SELECT * FROM TEST WHERE USER_NUM=? AND T_TITLE LIKE '%" + content
							+ "%' ORDER BY T_DATE DESC) TEST WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY T_DATE DESC";
				}
				// System.out.println("로그인 한경우 , content: " + content);

				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uvo.getUserNum());
				pstmt.setInt(2, (pageNum * pageSize) + pageSize);
				pstmt.setInt(3, pageNum * pageSize);

			} else {
				// System.out.println("로그인 안 한경우 + content : " + content);

				if (order.equals("댓글순")) { // RE_CNT
					System.out.println("댓글순");
					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST "
							+ "WHERE T_TITLE LIKE '%" + content + "%' ORDER BY RE_CNT DESC, T_DATE DESC) "
							+ "TEST WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY RE_CNT DESC";
					


				} else if (order.equals("조회순")) { // T_HIT
					System.out.println("조회순");
					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM (SELECT * FROM TEST "
							+ "WHERE T_TITLE LIKE '%" + content + "%' ORDER BY T_HIT DESC, T_DATE DESC) "
							+ "TEST WHERE ROWNUM <= ?) WHERE RNUM > ? ORDER BY T_HIT DESC";
				} else {
					System.out.println("최신순");
					sql = "SELECT * FROM (SELECT ROWNUM AS RNUM, TEST.* FROM ("
							+ "SELECT * FROM TEST WHERE T_TITLE LIKE '%" + content
							+ "%' ORDER BY T_DATE DESC) TEST WHERE ROWNUM <= ?"
							+ ") WHERE RNUM > ? ORDER BY T_DATE DESC";
				}
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, (pageNum * pageSize) + pageSize);
				pstmt.setInt(2, pageNum * pageSize);
			}
			rs = pstmt.executeQuery();

			ArrayList<TestVO> tlist = new ArrayList<TestVO>();

			while (rs.next()) {
				data = new TestVO();

				data.settId(rs.getInt("T_ID"));
				data.setUserNum(rs.getInt("USER_NUM"));
				data.settTitle(rs.getString("T_TITLE"));
				data.settContent(rs.getString("T_CONTENT"));
				data.settAnswer(rs.getString("T_ANSWER"));
				data.settEx(rs.getString("T_EX"));
				data.settWriter(rs.getString("T_WRITER"));
				data.settDate(rs.getDate("T_DATE"));
				data.settHit(rs.getInt("T_HIT"));
				data.settLang(rs.getString("T_LANG"));
				data.setReCnt(rs.getInt("RE_CNT"));
				tlist.add(data);
			}
			rs.close();

			if (uvo.getUserNum() > 0) {
				sql = "SELECT COUNT(*) FROM TEST WHERE USER_NUM =?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uvo.getUserNum());

			} else {
				sql = "SELECT COUNT(*) FROM TEST";
				pstmt = conn.prepareStatement(sql);

			}

			ResultSet total = pstmt.executeQuery();
			if (total.next()) {
				cnt = total.getInt(1);
			}
			total.close();

			datas.setTlist(tlist); // 게시글 모두
			datas.setTestCnt(cnt); // 게시글 총 갯수

		} catch (SQLException e) {
			System.out.println("TestDAO-getDBList 오류 로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		System.out.println("datas : " + datas);

		return datas;

	}
/////////////////////////////////////////////////////////////////////////	

	// getDBData --> 조회수 ++ (트랜잭션)
	@SuppressWarnings("resource")
	public TestVO getDBData(TestVO tvo, UsersVO uvo) {
		Connection conn = JNDI.getConnection();
		TestVO data = null;
		PreparedStatement pstmt = null;
		ResultSet rs;

		boolean check = false; // 트랜잭션 커밋, 롤백 여부 판단 변수
		try {

			// System.out.println("내가 쓴 글 클릭 -> 조회수증가xxx");
			// 내글을 클릭한경우면 -> 조회수 증가 xxxx ===> 셀렉트 원만됨
			pstmt = conn.prepareStatement(sql_SELECT_ONE);
			pstmt.setInt(1, tvo.gettId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				data = new TestVO();
				data.settId(rs.getInt("T_ID"));
				data.setUserNum(rs.getInt("USER_NUM"));
				data.settTitle(rs.getString("T_TITLE"));
				data.settContent(rs.getString("T_CONTENT"));
				data.settAnswer(rs.getString("T_ANSWER"));
				data.settEx(rs.getString("T_EX"));
				data.settWriter(rs.getString("T_WRITER"));
				data.settDate(rs.getDate("T_DATE"));
				data.settHit(rs.getInt("T_HIT"));
				data.settLang(rs.getString("T_LANG"));
				data.setReCnt(rs.getInt("RE_CNT"));
			}
			if (!(data.getUserNum() == uvo.getUserNum())) {
				// 내글이 아니면 -> 조회수 증가ooo ==> 트랜잭션을 이용
				conn.setAutoCommit(false); // 트랜잭션

				System.out.println("남의 글  클릭 -> 조회수증가ooo");

				pstmt = conn.prepareStatement(sql_HIT_UP);
				pstmt.setInt(1, tvo.gettId());
				pstmt.executeUpdate();
				check = true;

				if (check) {
					conn.commit();
				} else {
					conn.rollback();
				}
			}

			rs.close();

		} catch (SQLException e) {
			System.out.println("TestDAO-getDBData 오류로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return data;
	}
/////////////////////////////////////////////////////////////////////////	

	public boolean insert(TestVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql_INSERT);

			pstmt.setInt(1, vo.getUserNum());
			pstmt.setString(2, vo.gettTitle());
			pstmt.setString(3, vo.gettContent());
			pstmt.setString(4, vo.gettAnswer());
			pstmt.setString(5, vo.gettEx());
			pstmt.setString(6, vo.gettWriter());
			pstmt.setString(7, vo.gettLang());
			pstmt.executeUpdate();

			res = false;

		} catch (SQLException e) {
			System.out.println("TestDAO-insert 오류 로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}
/////////////////////////////////////////////////////////////////////////

	// update
	public boolean update(TestVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql_UPDATE);
			pstmt.setString(1, vo.gettTitle());
			pstmt.setString(2, vo.gettContent());
			pstmt.setString(3, vo.gettAnswer());
			pstmt.setString(4, vo.gettEx());
			pstmt.setString(5, vo.gettLang());
			pstmt.setInt(6, vo.gettId());
			pstmt.executeUpdate();
			res = true;
		} catch (SQLException e) {
			System.out.println("TestDAO-update 오류 로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}
/////////////////////////////////////////////////////////////////////////

	// delete
	public boolean delete(TestVO vo) {
		Connection conn = JNDI.getConnection();
		boolean res = false;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql_DELETE);
			pstmt.setInt(1, vo.gettId());
			pstmt.executeUpdate();
			res = true;
		} catch (SQLException e) {
			System.out.println("TestDAO-delete 오류 로깅");
			e.printStackTrace();
		} finally {
			JNDI.disconnect(pstmt, conn);
		}
		return res;
	}

/////////////////////////////////////////////////////////////////////////	

}
