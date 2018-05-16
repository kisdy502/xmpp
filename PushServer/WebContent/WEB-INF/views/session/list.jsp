<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Admin Console</title>
<meta name="menu" content="session" />
<link rel="stylesheet" type="text/css"
	href="<c:url value='/styles/tablesorter/style.css'/>" />
<script type="text/javascript"
	src="<c:url value='/scripts/jquery.tablesorter.js'/>"></script>

<script type="text/javascript">
$(function() {
	$('#tableList').tablesorter();
	$('table tr:nth-child(even)').addClass('even');	 
});
</script>
</head>
<body>
	<h1>Sessions</h1>
	<table id="tableList" class="tablesorter" cellspacing="1">
		<thead>
			<tr>
				<th align="center">Username</th>
				<th align="center">Resource</th>
				<th align="center">Status</th>
				<th align="center">Presence</th>
				<th align="center">Client IP</th>
				<th align="center">Created</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="sess" items="${sessionList}">
				<tr>
					<td align="center"><c:out value="${sess.username}" /></td>
					<td align="center"><c:out value="${sess.resource}" /></td>
					<td align="center"><c:out value="${sess.status}" /></td>
					<td align="center"><c:choose>
							<c:when test="${sess.presence eq 'Online'}">
								<img src="images/user-online.png" />
							</c:when>
							<c:when test="${sess.presence eq 'Offline'}">
								<img src="images/user-offline.png" />
							</c:when>
							<c:otherwise>
								<img src="images/user-away.png" />
							</c:otherwise>
						</c:choose> <c:out value="${sess.presence}" /></td>
					<td align="center"><c:out value="${sess.clientIP}" /></td>
					<td align="center"><fmt:formatDate
							pattern="yyyy-MM-dd HH:mm:ss" value="${sess.createdDate}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>