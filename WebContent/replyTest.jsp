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

<H2>로그인 XXX</H2>
<%
	ArrayList<TestReplySet> datas = new ArrayList<TestReplySet>();

	int pageNum = 0;

	//uvo.setUserNum(0);
	
	rvo.settId(1);
	
	
	datas = rdao.getDBList(pageNum, rvo);
	
	out.println(datas);
%>


<hr>
<H2>로그인 OOO</H2>

<%
TestMySet datas2= new TestMySet();

	pageNum = 0;

	uvo.setUserNum(1);
	
	//rvo.settId(1);
	
	
	datas2 = rdao.myReply(uvo, pageNum);
			
			
	
	out.println(datas2);

	
	
%>



</body>
</html>