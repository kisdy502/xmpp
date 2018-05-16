<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><decorator:title /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css"
	href="<c:url value='/styles/console.css'/>" />
<script type="text/javascript" src="<c:url value='/scripts/jquery.js'/>"></script>
<decorator:head />
</head>
<c:set var="topMenu" scope="request">
	<decorator:getProperty property="meta.menu" />
</c:set>
<c:set var="subMenu" scope="request">
	<decorator:getProperty property="meta.submenu" />
</c:set>
<body
	<decorator:getProperty property="body.id"
 writeEntireProperty="true"/>
	<decorator:getProperty property="body.onload" writeEntireProperty="true"/>>
	<div id="page">
		<div id="header">
			<jsp:include page="/includes/header.jsp" />
		</div>
		<div id="content">
			<ul id="tabmenu">
				<li><a href="index"
					class="<c:if test="${topMenu eq 'home'}"><c:out value="current" /></c:if>">Home</a></li>
				<li><a href="user"
					class="<c:if test="${topMenu eq 'user'}"><c:out value="current" /></c:if>">Users</a></li>
				<li><a href="session"
					class="<c:if test="${topMenu eq 'session'}"><c:out value="current" /></c:if>">Sessions</a></li>
				<li><a href="notification"
					class="<c:if test="${topMenu eq 'notification'}"><c:out value="current" /></c:if>">Notifications</a></li>
			</ul>
			<div id="tabcontent">
				<decorator:body/>			
			</div>
		</div>
		<div id="footer">
			<jsp:include page="/includes/footer.jsp" />
		</div>
	</div>
</body>
</html>