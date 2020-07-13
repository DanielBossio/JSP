<%-- 
    Document   : hacerpago
    Created on : 13/07/2020, 11:54:25 AM
    Author     : Win 8.1
--%>

<%@page import="java.util.Calendar"%>
<%@page import="bd.Pago"%>
<%@page import="bd.crud.ServicioJpaController"%>
<%@page import="bd.Servicio"%>
<%@page import="bd.Usuario"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="bd.crud.UsuarioJpaController"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="bd.crud.UsuarioJpaController"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%
    String refpago=request.getParameter("refpago");
    String monto=request.getParameter("monto");
    String cuenta=request.getParameter("cuenta");
    String clave=request.getParameter("clave");
    
    EntityManagerFactory con = Persistence.createEntityManagerFactory("bancoJSPPU");
    //Confirmar que el usuario este registrado
    UsuarioJpaController crudU = new UsuarioJpaController(con);
    List<Usuario> users = crudU.findUsuarioEntities();
    Gson gson = new Gson();
    String json ="";
    Usuario user = new Usuario();
    for (Usuario us : users){
        if (us.getCuenta().equals(cuenta)){
            user = us;
            break;
        }
    }
    if (user.equals(null)){
        json = "Usuario no registrado";
    }
    
    //confirmar que el servicio este registrado
    ServicioJpaController crudSer = new ServicioJpaController(con);
    List<Servicio> servs = crudSer.findServicioEntities();
    Servicio ser = new Servicio();
    for (Servicio s : servs){
        if (s.getRefPago().equals(refpago)){
            ser = s;
            break;
        }
    }
    if (ser.equals(null)){
        json = "Servicio inexistente";
    }
    
    if (!user.equals(null) && !ser.equals(null)){
        //Si se cumplen todas las condiciones se hace el pago
        float m = Float.parseFloat(monto);
        float val = ser.getMonto();
        if (val > m)
            ser.setMonto(val-m);
        else{
            ser.setMonto(Float.valueOf(0));
            ser.setIscancelado(true);
        }
        //Se registra el pago y se agrega al cliente
        String cod = refpago +" "+ Calendar.getInstance().toString() +" "+ monto;
        Pago p = new Pago(cod);
        p.setServicio(ser);
        p.setUsuario(user);
        user.agregarPago(p);
        json = gson.toJson(user);
        
    }
%>
<%= json %>
