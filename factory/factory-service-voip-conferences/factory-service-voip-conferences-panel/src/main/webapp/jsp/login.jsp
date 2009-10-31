<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
	<head>
		<title>Asterisk Conference Panel - Login</title>
		<link rel="stylesheet" type="text/css" href="css/style.css" media="all" />
		<link rel="stylesheet" type="text/css" title="default" media="all" href="css/qualipso/template_csas.css" />
	</head>	
	<body style="width: 370px;">
		<h1>Login</h1>

		<div style='margin: 1em 0 1em 1em; border-left: 2px solid black; padding-left: 1em;'>
		    <form method="post">
		        <strong>name*:</strong> <br/>
		        <input style="width: 18em;" type="text" size="40" 
		        	value=""
		        	name="userid"/><br />
		        <input type="submit" value="Login"/>
            <strong>name*:</strong> <br/>
            <input style="width: 18em;" type="text" size="40" 
              value=""
              name="project"/><br />
            <input type="submit" value="Login"/>
		    </form>
		    <p>* - <em>put any name (ex. test, janny, qualipso, etc.)</em></p>
		</div>
	</body>
</html>