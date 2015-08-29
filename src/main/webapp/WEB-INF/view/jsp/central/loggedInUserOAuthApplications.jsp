<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${!empty userApplications}">
    <div id="user-applications-form-group-${accountId}" class="form-group">
        <label class="control-label">Connected Apps</label>

        <ul id="user-applications-ul-${accountId}" class="user-applications list-group">
            <c:forEach var="userApplication" items="${userApplications}">
                <li id="disconnect-list-item-${userApplication.id}" class="user-application list-group-item list-group-item-info">
                    <div class="row">
                        <div class="col-xs-7">
                            <div class="list-group-item-text">
                                    ${userApplication.name}
                            </div>
                        </div>
                        <div class="col-xs-5">
                            <a href="#" class="disconnect-user-application" data-account-id="${accountId}" data-user-application-id="${userApplication.id}" data-client-id="${userApplication.clientId}">Disconnect</a>
                        </div>
                    </div>
                </li>
            </c:forEach>

            <li class="list-group-item">
                <div class="row">
                    <div class="col-xs-5 pull-right">
                        <a class="disconnect-all-applications" href="#" data-account-id="${accountId}">Disconnect All</a>
                    </div>
                </div>
            </li>
        </ul>


        <c:forEach var="userApplication" items="${userApplications}">
            <div id="disconnect-confirm-${userApplication.id}" class="well hide">
                <form:form id="disconnect-confirm-form-${userApplication.id}">
                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12 text-center">
                                Are you sure you want to disconnect
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 text-center">
                                <strong>${userApplication.name}</strong>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12 text-center">
                                <button type="button" data-account-id="${accountId}" data-user-application-id="${userApplication.id}" class="btn btn-link disconnect-cancel">Cancel</button>
                                <button type="button" data-account-id="${accountId}" data-client-id="${userApplication.clientId}" data-user-application-id="${userApplication.id}" class="btn btn-sm btn-primary disconnect">Disconnect</button>
                            </div>
                        </div>
                    </div>
                </form:form>
            </div>
        </c:forEach>

        <div id="disconnect-confirm-all" class="well hide">
            <form:form id="disconnect-confirm-form-all">
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-12 text-center">
                            Are you sure you want to disconnect all applications?
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-12 text-center">
                            <button type="button" data-account-id="${accountId}" data-user-application-id="${userApplication.id}" class="btn btn-link disconnect-all-cancel">Cancel</button>
                            <button type="button" data-account-id="${accountId}" data-client-id="${userApplication.clientId}" data-user-application-id="${userApplication.id}" class="btn btn-sm btn-primary disconnect-all">Disconnect</button>
                        </div>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</c:if>
