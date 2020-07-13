<%-- 
    Document   : hacertransfer
    Created on : 13/07/2020, 12:31:38 PM
    Author     : Win 8.1
--%>

<%@page import="bd.Transferencia"%>
<%@page import="java.util.Calendar"%>
<%@page import="bd.CuentaExt"%>
<%@page import="bd.BancoExt"%>
<%@page import="bd.crud.BancoExtJpaController"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="java.util.List"%>
<%@page import="bd.Usuario"%>
<%@page import="bd.crud.UsuarioJpaController"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%
    
    String banco=request.getParameter("banco");
    String cuentaex=request.getParameter("cuentaex");
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
    
    //confirmar que el banco y la cuenta externa esten registrados
    BancoExtJpaController crudBan = new BancoExtJpaController(con);
    List<BancoExt> bancos = crudBan.findBancoExtEntities();
    BancoExt ban = new BancoExt();
    for (BancoExt b : bancos){
        if (b.getNombre().equals(banco)){
            ban = b;
            break;
        }
    }
    if (ban.equals(null)){
        json = "Banco no asociado";
    }
    List<CuentaExt> cuentas = ban.getCuentaExtList();
    CuentaExt cuen = new CuentaExt();
    for (CuentaExt c : cuentas){
        if (c.getNumcuenta().equals(cuentaex)){
            cuen = c;
            break;
        }
    }
    if (cuen.equals(null)){
        json = "Cuenta inexistente";
    }
    
    
    if (!user.equals(null) && !ban.equals(null)){
        //Si se cumplen todas las condiciones se hace la transferencia
        float m = Float.parseFloat(monto);
        cuen.setMonto(cuen.getMonto()+m);
        //Se registra el pago y se agrega al cliente
        String cod = cuen.getNumcuenta() +" "+ Calendar.getInstance().toString() +" "+ monto;
        Transferencia t = new Transferencia(cod);
        t.setCuenta(cuen);
        t.setUsuario(user);
        user.agregarTransfer(t);
        json = gson.toJson(user);
        
    }
%>
<%= json %>
