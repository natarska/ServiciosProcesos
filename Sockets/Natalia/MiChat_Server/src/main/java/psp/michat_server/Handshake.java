/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp.michat_server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dirrospace
 */
@WebServlet(name = "Handshake", urlPatterns = {"/Handshake"})
public class Handshake extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       String tipo = request.getParameter(Constantes.ATTTIPO);
        Seguridad as = new Seguridad();
        String miclaveComun = null;
        switch (tipo) {
            case "GivePUK":
                // proceso clave publica: bytes[] > encode bytes[] > String
                InputStream is = this.getClass().getResourceAsStream("/claves/LoginProject.publica");
                byte[] clavePublica = as.obtenerPublicaBytes_Fichero(null, null,is);
                response.getOutputStream().write(org.apache.commons.codec.binary.Base64.encodeBase64(clavePublica));
                break;
            case "ComunKeySecurity":
                InputStream inp = this.getClass().getResourceAsStream("/claves/LoginProject.privada");
                
                String recib64Json = (String) request.getParameter(Constantes.ATTKEY);
                if (recib64Json != null) {
                    miclaveComun = as.decode_PRK(
                            as.prk_byteTOPRK(
                                as.obtenerPrivadaBytes_Fichero(null, null, inp)
                            ),
                            Base64.decodeBase64(recib64Json));
                }
                String mandar = null;
                if (miclaveComun != null) {
                    request.getSession().setAttribute(Constantes.ATTKEY, miclaveComun);
                    mandar = new String("ok");

                } else {
                    mandar = new String("false");
                }
                response.getOutputStream().print(mandar);
                break;
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
