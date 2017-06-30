<%@ page import="grails.util.Environment" %>
<!doctype html>
<html>
<head>
  <title><g:if env="development">Grails Runtime Exception</g:if><g:else>Error</g:else></title>
  <meta name="layout" content="register">
  <g:set var="security" bean="modelCatalogueSecurityService"/>
  <g:if test="${Environment.DEVELOPMENT == Environment.current || security.hasRole('STACKTRACE')}"><asset:stylesheet src="errors.css"/></g:if>
</head>
<body>
<g:if test="${Environment.DEVELOPMENT == Environment.current || security.hasRole('STACKTRACE')}">

  <g:if test="${Throwable.isInstance(exception)}">
    <g:renderException exception="${exception}" />
  </g:if>
  <g:elseif test="${request.getAttribute('javax.servlet.error.exception')}">
    <g:renderException exception="${request.getAttribute('javax.servlet.error.exception')}" />
  </g:elseif>
  <g:else>
    <ul class="errors">
      <li>An error has occurred</li>
      <li>Exception: ${exception}</li>
      <li>Message: ${message}</li>
      <li>Path: ${path}</li>
    </ul>
  </g:else>
</g:if>
<g:else>
  <div class="col-md-6 col-md-offset-3">
    <div class="alert alert-danger">An error has occurred</div>
  </div>
</g:else>
</body>
</html>
