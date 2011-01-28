<%@page import="de.l3s.interwebj.bean.RegistrationBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	RegistrationBean registerBean = (RegistrationBean) request.getAttribute("interwebj.registartion.bean");
	if (registerBean == null)
	{
		registerBean = new RegistrationBean();
	}
%>
<p>Fill in all inputs</p>
<form action="register" method="post">
<table border="0">
	<tr>
		<td><b>Username: </b></td>
		<td><input type="text" size="15" name="userName"
			value="<%=registerBean.getUserName()%>" /></td>
		<td id="error"><%=registerBean.getErrorMessage("userName")%></td>
	</tr>
	<tr>
		<td><b>Password: </b></td>
		<td><input type="password" size="15" name="password"
			value="<%=registerBean.getPassword()%>" /></td>
		<td id="error"><%=registerBean.getErrorMessage("password")%></td>
	</tr>
	<tr>
		<td><b>Confirm Password: </b></td>
		<td><input type="password" size="15" name="password2"
			value="<%=registerBean.getPassword2()%>" /></td>
		<td id="error"><%=registerBean.getErrorMessage("password2")%></td>
	</tr>
	<tr>
		<td><b>E-mail: </b></td>
		<td><input type="text" size="15" name="email"
			value="<%=registerBean.getEmail()%>" /></td>
		<td id="error"><%=registerBean.getErrorMessage("email")%></td>
	</tr>
	<tr>
		<td colspan="3"><input type="submit" value="Send" /></td>
	</tr>
</table>
</form>