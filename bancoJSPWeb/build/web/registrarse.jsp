<%-- 
    Document   : registrarse
    Created on : 13/07/2020, 11:37:25 AM
    Author     : Win 8.1
--%>

<%@page import="com.google.gson.Gson"%>
<%@page import="bd.Usuario"%>
<%@page import="java.util.List"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="bd.crud.UsuarioJpaController"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%
    String nombre=request.getParameter("nombre");
    String email=request.getParameter("email");
    String cuenta=request.getParameter("cuenta");
    String clave=request.getParameter("clave");
    
    EntityManagerFactory con = Persistence.createEntityManagerFactory("bancoJSPPU");
    UsuarioJpaController crudU = new UsuarioJpaController(con);
    List<Usuario> users = crudU.findUsuarioEntities();
    Gson gson = new Gson();
    String json ="";
    for (Usuario us : users){
        if (us.getNombre().equals(nombre)){
            json = "Usuario ya registrado";
        }
        else if (us.getCuenta().equals(cuenta)){
            json = "Cuenta ya registrada";
        }
    }
    if (json.equals("")){
        Usuario us = new Usuario(nombre, email, cuenta, clave);
        crudU.create(us);
        json = gson.toJson(us);
    }
%>
<%= json %>
