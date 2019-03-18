<%--
  Created by IntelliJ IDEA.
  User: Artress
  Date: 24.02.2019
  Time: 16:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User profile</title>
</head>
<body>
    <h1>User profile</h1>
    <br/>ID: ${user.id}
    <br/>Login: ${user.login}
    <br/>E-mail: ${user.email}
    <br/>Password: ${user.password}
    <br/>Country: ${user.country}
    <br/>City: ${user.city}
    <br/>Role: ${user.role}
    <br/>
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