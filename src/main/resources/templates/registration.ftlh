<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
</head>
<body>

<h1>Registration</h1><hr>

<form action="/registration" method="POST">
    Email: <input type="email" name="email"><br><br>
    Password: <input type="password" name="password"><br><br>

    <label for="role">Role:</label>
    <select name="role" id="role">
        <#list roles as role>
            <option value="${role}">${role?capitalize}</option>
        </#list>
    </select><br>

    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <input type="submit" value="Sign up"/>

</form>

<#if errorMessage??>
    <h2 style="color: red"> ${errorMessage}</h2>
</#if>

</body>
</html>

