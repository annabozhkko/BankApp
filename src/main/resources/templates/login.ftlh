<!DOCTYPE html>
<html>
<head>
    <title>Authorization</title>
</head>
<body>
<h1>Authorization</h1><hr>

<form action="/login" method="POST">
    Email: <input type="email" name="username"><br><br>
    Password: <input type="password" name="password"><br><br>
    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <input type="submit" value="Sign in"/>
</form>
<a href="/registration">Sign up</a><br>
<a href="/forgotPassword">Forgot password</a>
</body>
</html>
