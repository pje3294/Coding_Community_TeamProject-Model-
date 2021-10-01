<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*, model.test.*"%>


<jsp:useBean id="tvo" class="model.test.TestVO" />
<jsp:setProperty property="*" name="tvo" />
<jsp:useBean id="tdao" class="model.test.TestDAO" />
<jsp:useBean id="rvo" class="model.test.TestReplyVO" />
<jsp:setProperty property="*" name="rvo" />
<jsp:useBean id="rdao" class="model.test.TestReplyDAO" />
<jsp:useBean id="uvo" class="model.users.UsersVO" />
<jsp:useBean id="udao" class="model.users.UsersDAO" />
<jsp:setProperty property="*" name="udao" />


<%
	String action = request.getParameter("action");
	String content ="코";


	String url = "control.jsp?action=main";

	//페이징 위해 (페이지유지의 핵심!)
	String mcntt = request.getParameter("pageCnt");
	int pageCnt = 1;
	if (mcntt != null) {
		pageCnt = Integer.parseInt(mcntt);
	}
	url = url + "&mcnt=" + pageCnt;
	String selUser = request.getParameter("selUser");
	if (selUser != null) {
		url = url + "&selUser=" + selUser; //selUser : 내글보기 / 검색한 사용자 보기 
	}
	
	
	if(action.equals("main")){
		System.out.println("메인 컨트롤");
		
		ArrayList<TestVO> datas = tdao.getDBList(content, pageCnt, uvo);
		
		

		request.setAttribute("datas", datas);
		request.setAttribute("selUser", selUser);
		request.setAttribute("pageCnt", pageCnt);
		
		request.setAttribute("datas", datas);
		
		pageContext.forward("view.jsp");
	} else if (action.equals("login")) {
		uvo.setId(request.getParameter("id"));
		uvo.setPw(request.getParameter("pw"));
	
		if (udao.login(uvo)) { // 로그인성공
	session.setAttribute("seUser", uvo.getId());
	response.sendRedirect(url);
		} else {
	out.println("<script>alert('로그인 실패!!! 확인이 필요합니다.');history.go(-1);</script>");
		}
	} else if (action.equals("logout")) {
		session.invalidate();
		response.sendRedirect("control.jsp?action=main");

	}else if(action.equals("detail")){
		rvo.settId(Integer.parseInt(request.getParameter("tid")));
		ArrayList<TestReplySet> datas2 = rdao.getDBList(uvo, pageCnt, rvo);
		request.setAttribute("datas2", datas2);
	}
%>