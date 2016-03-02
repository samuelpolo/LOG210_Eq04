<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Créer un compte gestionnaire</title>
</head>
<body>
	

   <form action="LoginServlet" method="post">
      email : <input type="text" name="login" />
      mot de passe : <input type="password" name="password" />
      nom de la cooperative : <input type="text" name="nomCoop" />
      adresse de la cooperative : <input type="text" name="addressCoop" />
      <input type="submit" value="Créer le gestionnaire" />
   </form>

</body>
</html>