<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*, model.test.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="tvo" class="model.test.TestVO" />
<jsp:setProperty property="*" name="tvo" />
<jsp:useBean id="tdao" class="model.test.TestDAO" />
<jsp:useBean id="rvo" class="model.test.TestReplyVO" />
<jsp:setProperty property="*" name="rvo" />
<jsp:useBean id="rdao" class="model.test.TestReplyDAO" />
<jsp:useBean id="uvo" class="model.users.UsersVO" />
<jsp:useBean id="udao" class="model.users.UsersDAO" />
<jsp:setProperty property="*" name="udao" />


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<%-- 	<H2>로그인 XXX</H2>
	<%
		/* tvo.settId(1);
		tvo.settWriter("j");
		
		uvo.setId("je");
		
		TestVO data = tdao.getDBData(tvo, uvo);
		
		out.println(data); */

		TestSet datas = new TestSet();

		String content = "";
		int pageNum = 1;
		String order = "댓글순";

		uvo.setUserNum(0);
		datas = tdao.getDBList(content, pageNum, uvo, order);
		
		
		
	%>
	 --%>
  <h2>게시판 출력</h2>
   <%
      //=========================================================================
      // 게시판 전체 출력
      
      uvo.setUserNum(0);          // 내가쓴글 전체 출력 할때 userNum 입력
      String content = "";
      int pageNum = 2;
      String order ="";
      
      
      TestSet datas = new TestSet();
      datas = tdao.getDBList(content, pageNum, uvo, order);
      
      
      for (TestVO v : datas.getTlist()) {
         // System.out.println(v);
         out.println("<hr>");
         out.println(v+"<hr>");
         out.println("<hr>");
      } 
      //=========================================================================
            
      /* uvo.setUserNum(1);   // 내가 쓴 글이면 조회수 증가 안함
      bvo.setbId(1);
      
      
      bdata = bDAO.getDBData(uvo, bvo);
      System.out.println(bdata);
      out.println(bdata); */
      
   %>
	


	<%-- <%
	uvo.setUserNum(0);

	int cnt = rdao.testCnt(uvo);
	
	out.println(cnt);
%> --%>

	<hr>
	<H2>로그인 OOO</H2>

	<%

		/* content = "";
		pageNum = 1;
		order = "";

		uvo.setUserNum(3);
		datas = tdao.getDBList(content, pageNum, uvo, order);

		out.println(datas); */
	%>



	<%-- <%
ArrayList<TestSet> datas2 = new ArrayList<TestSet>();

uvo.setUserNum(1);
rvo.setrId(1);
pageCnt = 2;

datas2 = rdao.getDBList(uvo, pageCnt, rvo);
out.println(datas);

%>  --%>


</body>
</html>