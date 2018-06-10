<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    request.setAttribute("basePath", basePath);
%>
<%--common会是这个system的头部 我在web.xml中配置--%>