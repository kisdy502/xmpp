<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Admin Console</title>
<meta name="menu" content="user" />
<link rel="stylesheet" type="text/css"
	href="<c:url value='/styles/tablesorter/style.css'/>" />
<script type="text/javascript"
	src="<c:url value='/scripts/jquery.tablesorter.js'/>"></script>
</head>
<body>
	<h1>Users</h1>
	<table id="tableList" class="tablesorter" cellspacing="1">
		<thead>
			<tr>
				<th>Online</th>
				<th>Username</th>
				<th>Name</th>
				<th>Email</th>
				<th>Created</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="user" items="${userList}">
				<tr>
					<td align="center"><c:choose>
							<c:when test="${user.online eq true}">
								<img src="images/user-online.png" />
							</c:when>
							<c:otherwise>
								<img src="images/user-offline.png" />
							</c:otherwise>
						</c:choose></td>
					<td><c:out value="${user.username}" /></td>
					<td><c:out value="${user.name}" /></td>
					<td><c:out value="${user.email}" /></td>
					<td align="center"><fmt:formatDate
							pattern="yyyy-MM-dd HH:mm:ss" value="${user.createdDate}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>