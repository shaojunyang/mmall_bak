
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>hello</h1>
springMVC上传图片
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" value="springMVC上传文件">
</form>

富文本上传图片
<form name="form1" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" value="springMVC上传文件"/>
</form>
</body>
</html>
