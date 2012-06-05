<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Editing Employee ${lohikaEmployee.id} - ${lohikaEmployee.lastName}  ${lohikaEmployee.firstName}  
</h1>
<form:form commandName="lohikaEmployee" style="padding:8px">
    ID - ${lohikaEmployee.id}<br/>
    <p>
        First Name:<br/>
        <form:errors path="firstName" cssStyle="color:red" /><br />
        <form:input path="firstName"/>
    </p>
    <p>
        Last Name:<br/>        
        <form:errors path="lastName" cssStyle="color:red" /><br />
        <form:input path="lastName"/>
    </p>
    <p>
        Project:<br/>
        <form:errors path="project" cssStyle="color:red" /><br />
        <form:input path="project"/>
    </p>
    
    <input type="submit" value="Save"/>
</form:form>
</body>
</html>
