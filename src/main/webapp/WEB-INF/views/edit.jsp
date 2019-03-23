<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Artress
  Date: 19.02.2019
  Time: 23:58
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Update user</title>
</head>
<body>
<form action="${pageContext.servletContext.contextPath}/edit" method="post">
    <table>
        <tr><td>ID:</td><td><input type="text" readonly name="id" value="${user.id}"/></td></tr>
        <tr><td>Login:</td><td><input type="text" name="login" value="${user.login}"/></td></tr>
        <tr><td>E-mail:</td><td><input type="text" name="email" value="${user.email}"/></td></tr>
        <tr><td>Password:</td><td><input type="password" name="password" value="${user.password}"/></td></tr>
        <tr><td>Country:</td><td><input type="text" name="country" value="${user.country}"/></td></tr>
        <tr><td>City:</td><td><input type="text" name="city" value="${user.city}"/></td></tr>
        <c:if test="${current_user.role == 'admin'}">
            <tr><td>Role:</td><td>
                <select name="role">
                    <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Administrator</option>
                    <option value="user" ${user.role == 'user' ? 'selected' : ''}>User</option>
                </select>
            </td></tr>
        </c:if>
        <c:if test="${current_user.role == 'user'}">
            <input type="hidden" name="role" value="${user.role}">
        </c:if>
        <tr><td><input type="submit" value="Edit"></td></tr>
    </table>
</form>
<br/>
<form action="${pageContext.servletContext.contextPath}/list" method="get">
    <input type="submit" value="List">
</form>
<br/>
<form action="${pageContext.servletContext.contextPath}/logout" method="get">
    <input type="submit" value="Exit">
</form>
</body>
</html>