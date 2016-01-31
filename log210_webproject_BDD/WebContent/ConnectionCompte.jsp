<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Connection à un compte</title>
</head>
<body>
	   <form action="connectionCompte" method="post">
	      courriel ou numéro de téléphone : <input type="text" name="login" />
	      mot de passe : <input type="password" name="password" />
	      <input type="submit" value="connexion" />
	   </form>
</body>
</html>