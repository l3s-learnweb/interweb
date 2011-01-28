<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="topnav">
<ul>
	<c:choose>
		<c:when test="${empty pageContext.request.remoteUser}">
			<li><a href="${pageContext.request.contextPath}/login.jsp">Login</a></li>
			<li><a href="${pageContext.request.contextPath}/register.jsp">Register</a></li>
		</c:when>
		<c:otherwise>
			<li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
			<li><a href="${pageContext.request.contextPath}/profile.jsp">Profile</a></li>
		</c:otherwise>
	</c:choose>
	<li id="skip"><a href="${pageContext.request.contextPath}/">Home</a></li>
</ul>
</div>
<div id="header"><span>InterWebJ</span></div>