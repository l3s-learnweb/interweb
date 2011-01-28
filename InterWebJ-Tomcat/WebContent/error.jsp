<%@page import="de.l3s.interwebj.tomcat.IWResponseWrapper"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>InterWebJ</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>
<%@ include file="blocks/header_block.jsp"%>
<div id="wrapper">
<div id="content-wrapper">
<div id="content">
<p id="error">
<c:out value="${requestScope['interwebj.error.message']}" />
<c:choose>
	<c:when
		test="${requestScope['interwebj.error.code'] == '401'}">
		<%@ include file="blocks/login_form_block.jsp"%>
	</c:when>
	<c:when test="${requestScope['interwebj.error.code'] == '403'}">
		<!--br/>
		<a href="${sessionScope['de.l3s.interwebj.tomcat.IWSecurityFilter.SAVED_URL']}">Back</a-->
	</c:when>
</c:choose></p>
</div>
</div>
</div>
<%@ include file="blocks/footer_block.jsp"%>
</body>
</html>
