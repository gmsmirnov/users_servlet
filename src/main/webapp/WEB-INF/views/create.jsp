<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.job4j.servlets.Constants" %>
<%--
  Created by IntelliJ IDEA.
  User: Artress
  Date: 19.02.2019
  Time: 23:56
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create user</title>
</head>
<body>
<form action="${pageContext.servletContext.contextPath}/create" method="post">
    Login: <input type="text" name="login"/></br>
    E-mail: <input type="text" name="email"/></br>
    Password: <input type="password" name="password"/></br>
    Country: <input type="text" name="country"/></br>
    City: <input type="text" name="city"/></br>
    Role:
        <select name="role">
            <c:if test="${current_user.role == 'admin'}">
                <option value="admin">Administrator</option>
            </c:if>
            <option value="user" selected>User</option>
        </select>
    <input type="submit" value="Create">
</form>
<form action="${pageContext.servletContext.contextPath}/list" method="get">
    <input type="submit" value="List">
</form>
<br/>
<form action="${pageContext.servletContext.contextPath}/logout" method="get">
    <input type="submit" value="Exit">
</form>
</body>
</html>