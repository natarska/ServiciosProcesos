package proxy;

/**
 *
 * @author dirrospace
 */
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;

// MYENDPOINY
// Enganchas del endpoint de servidor un listener a onOpen de aqui > desencadena una accion   !!!COMPROBAR
// Tipo de recepcion: binario o texto (en json y cifrado en B64)
// Tipo de comunicacion: a traves de un protocolo
public class Proxy extends Endpoint{
    // ATT ACCESO DE CONEXION
    // Usado para mantener la conexion abierta como puerta de acceso.
    private Session sesion;

    // Comunicacion URI hacia donde
    public Proxy(Session sesion, URI urlend) {
        this.sesion = sesion;
        
        try {
            final ClientEndpointConfig cec = 
                    ClientEndpointConfig.Builder.create().configurator(
                            new ClientEndpointConfig.Configurator() { 
                                @Override 
                                public void beforeRequest(Map<String, List<String>> headers) { 
                                    // olvida de mmento
                                    super.beforeRequest(headers);
                                                 //String sessionId = login();
                                                 List cookieList = headers.get("Cookie");
                                                 if (cookieList == null) cookieList = new ArrayList();
                                                 cookieList.add("JSESSIONID=\""+sesion+"\"");
                                                 headers.put("Cookie", cookieList);

                                } 
                    }).build(); 
            // lo construyes y lo metes al sever
            ClientManager client = ClientManager.createClient();
            // connect to server es endpoint y entonces tiene metodos. Cuando se conecta salta evento
            client.connectToServer (this, cec,urlend);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
  
    
    

    @Override
    public void onOpen(Session sn, EndpointConfig ec) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
