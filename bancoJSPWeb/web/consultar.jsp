<%-- 
    Document   : consultar
    Created on : 12/07/2020, 05:02:21 PM
    Author     : Win 8.1
--%>

<%@page import="bd.Usuario"%>
<%@page import="java.util.List"%>
<%@page import="bd.crud.UsuarioJpaController"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@page import="com.google.gson.Gson"%>
<%
    String cuenta=request.getParameter("cuenta");
    String clave=request.getParameter("clave");
    
    EntityManagerFactory con = Persistence.createEntityManagerFactory("bancoJSPPU");
    UsuarioJpaController crudU = new UsuarioJpaController(con);
    List<Usuario> users = crudU.findUsuarioEntities();
    Gson gson = new Gson();
    String json ="";
    for (Usuario us : users){
        if (us.getCuenta().equals(cuenta) && us.getClave().equals(clave)){
            json = gson.toJson(us);
            break;
        }
    }
%>
<%= json %>
