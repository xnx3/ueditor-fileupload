<%@page import="com.xnx3.j2ee.Global"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.xnx3.com/java_xnx3/xnx3_tld" prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/wm/common/head.jsp">
	<jsp:param name="title" value="test"/>
</jsp:include>

<!-- 配置文件 -->
<script type="text/javascript" src="/module/ueditor/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="/module/ueditor/ueditor.all.js"></script>

<textarea class="layui-input" id="myEditor" name="text" style="height: auto; padding-left: 0px; border: 0px;"></textarea>
<!-- 实例化编辑器 -->
<script type="text/javascript">
var ueditorText = document.getElementById('myEditor').innerHTML;
var ue = UE.getEditor('myEditor',{
	autoHeightEnabled: true,
	autoFloatEnabled: true,
	initialFrameHeight:460  
});
//对编辑器的操作最好在编辑器ready之后再做
ue.ready(function() {
	document.getElementById("myEditor").style.height='auto';
});
</script>