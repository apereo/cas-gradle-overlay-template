<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">

    #welcome {
        width: 390px;
        margin: 100px auto 0 auto;
        text-align: center;
        font-size: 18px;
    }

    #success {
        color: #60a939;
        text-align: center;
        margin: 0 0 20px 0;
    }

    #register {
        width: 330px;
        padding: 30px;
        margin: 10px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

</style>

<div id="welcome">
    Welcome, ${user.firstName}!<br/>
    Please Create Your Infusionsoft ID
</div>

<div id="register">
    <c:url var="centralUrl" value="/login"/>
    <div id="success">
        Your ID has been created successfully!
    </div>

    <div class="usercard">
        <img class="photo"/>
        <div class="name">${user.firstName} ${user.lastName}</div>
        <div class="info">
            ${user.username}<br/>
            password: ***********
        </div>
        <div style="clear: both"></div>
    </div>

    <div style="text-align: right; margin-top: 15px">
        <a class="btn btn-primary" href="${centralUrl}">Go To Your App</a>
    </div>
</div>