<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<h3>Formulaire de connexion</h3>
    
<div>
	<p>Cr�er un nouveau compte gestionnaire</p>
	   <form action="LoginServlet" method="post">
	      login : <input type="text" name="login" />
	      password : <input type="password" name="password" />
	      <input type="submit" value="connexion" />
	   </form>
</div>

</body>
</html>