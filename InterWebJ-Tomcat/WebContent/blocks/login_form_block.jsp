<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<p>Enter your user name and password to log in</p>
<form action="j_security_check" method="post">
<table border="0">
	<tr>
		<td><b>Username: </b></td>
		<td><input type="text" size="15" name="j_username"></td>
	</tr>
	<tr>
		<td><b>Password: </b></td>
		<td><input type="password" size="15" name="j_password"></td>
	</tr>
	<tr>
		<td></td>
		<td align="right"><input type="submit" value="Submit"></td>
	</tr>
</table>
</form>
