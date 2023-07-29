<!DOCTYPE html>
<html>
<head>
    <title>Password recovery</title>
</head>
<body>

<h1>Password recovery</h1><hr>

<form action="/forgotPassword" method="POST">
    Email: <input type="email" name="email"><br><br>

    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <input type="submit" value="Reset Password">
</form>

<#if errorMessage??>
    <h2 style="color: red"> ${errorMessage}</h2>
</#if>

</body>
</html>