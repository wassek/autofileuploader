<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>


<div class="page-header">
    <h2><fmt:message key="label.autofileUploaderAdmin"/></h2>
</div>


<c:set var="serverPath" value="${autofileuploadersettings.serverPath}"/>
<c:set var="intervall" value="${autofileuploadersettings.intervall}"/>
<c:if test="${autofileuploadersettings.autopublish == 'true'}">
    <c:set var="autopublish" value="checked"/>
</c:if>

<div class="panel panel-default">
        <div class="box-1">
          
           <form style="margin: 0;" action="${flowExecutionUrl}" method="post">
                <input type="hidden" name="save" value="1"/>
            <fieldset>
                <div class="container-fluid">
                    <div class="row-fluid">
                        <div class="span4">
                            <label for="firstName"><fmt:message key="label.serverPath"/></label>
                            <input type="text" value="${serverPath}" name="serverPath" />
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="span4">
                            <label for="email"><fmt:message key="label.intervall"/></label>
                            <input type="text" value="${intervall}" name="intervall" />
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="span4">
                            <label for="email"><fmt:message key="label.autopublish"/></label>
                            <input type="checkbox" ${autopublish} name="autopublish" />
                        </div>
                    </div>
                </div>      
             </fieldset>
             <fieldset>
                <div class="container-fluid">
                    <div class="row-fluid">
                        <div class="span4">
                               <button class="btn btn-primary" type="submit" name="_eventId_saveSettings">
                                <i class="icon-ok icon-white"></i>
                                &nbsp;<fmt:message key='label.update'/>
                            </button>
                        </div>
                    </div>

                </div> 
             </fieldset>
      
      
                   


                        
       </form>      
    
    </div>
</div>

