/*
No hay implementacion estandar. Socket java en los dos lados y la comunicacion 
Socket: protocolo ws para que funcione desde cualquier pagina web
Los clientes de web-socket la implementacion es tauros que funciona con glashfish, cada servidor tiene su implementacion
Apache common: petcion http con apache comon el servidor de google lo refusa. El otra forma mas tedioso con = filosofia
Google php : como se firma certificado con CA consulta par manejarlo


 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package dam.chatclient;


import com.datoshttp.Mensaje;
import com.datoshttp.MetaMensajeWS;
import com.datoshttp.OrdenRoomsWS;
import com.datoshttp.TipoMensaje;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;

/**
 * @author Arun Gupta
 */

public class MyClient extends Endpoint {
    // No es sesion http sino es el acceso a la conexion que se quede abierta. Como puerta de acceso digo yo
    private Session userSession;
    // Engancha un listener al endpoint a ese acceso le pones un evento que es este : y asi desde fuera se llama a un evento que se engancha a una caja de
    // texto(en json y cifrado es en B64). Con websocket(son msg de texto q tmbn hay binario y sobre ellos un protocolo) puede tener acceso a mensaje de control
    
    private MessageListener messageHandler;
    // En el cliente necesitas un endpoint  y una url (uri)
    public MyClient( URI endpointURI, String sessionId) {
         try {
            final ClientEndpointConfig cec;
            
            cec = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() { 
            @Override 
            public void beforeRequest(Map<String, List<String>> headers) { 
                // olvida de mmento
                super.beforeRequest(headers);
                             //String sessionId = login();
                             List cookieList = headers.get("Cookie");
                             if (cookieList == null) cookieList = new ArrayList();
                             cookieList.add("JSESSIONID=\""+sessionId+"\"");
                             headers.put("Cookie", cookieList);

            } 
            }).build(); 
            // lo construyes y lo metes al sever
            ClientManager client = ClientManager.createClient();
            // connect to server es endpoint y entonces tiene metodos. Cuando se conecta salta evento
            client.connectToServer (this, cec,endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    // hacemos handler en onopen cuandollegue el mensaje salt est
    @Override
    public void onOpen(Session session, EndpointConfig ec) {
        // En cliente solo hay una conexion abierta. Aqui la sesion se guarda para que el puntero a la conexion abierta este
        // montas ahora despues el evento del mensaje
       this.userSession = session;
       // se va a process mesage () metodo
       session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                processMessage(message);
            }
        });
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
    }
//    Tiene un esto para poder hacerlo o en el onopen le haces un abre que seria el metodo a manejar cuando se conectase
//    @Override
//    public void onMessage(){
//        
//    } 
    
    public void addMessageHandler(final MessageListener msgHandler) {
        messageHandler = msgHandler;
    }

    public void sendMessage(Mensaje message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MetaMensajeWS ms = new MetaMensajeWS();
            ms.setTipo(TipoMensaje.MENSAJE);
            ms.setContenido(mapper.writeValueAsString(message));
            String men = mapper.writeValueAsString(ms);
            
            //encriptar el men con la key
            
            userSession.getAsyncRemote().sendText(men);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     public void sendOrden(OrdenRoomsWS orden) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MetaMensajeWS ms = new MetaMensajeWS();
            ms.setTipo(TipoMensaje.ORDEN);
            ms.setContenido(mapper.writeValueAsString(orden));
            String men = mapper.writeValueAsString(ms);
            userSession.getAsyncRemote().sendText(men);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Cuando conecta que llama al atributo de la clase mesage y cuando haga lo que tenga que hacer llama al metodo de esta interfaz
     // el que recibe los mensajes
     // ¿COMO LO ENGANCHO CON HAT NORMAL VETE A PQUE CHT FRAME.JAVA?
    public void processMessage(String message) {
        if (messageHandler != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Mensaje mensaje = mapper.readValue(message,
                        new TypeReference<Mensaje>() {
                });

                messageHandler.handleMessage(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

     public static interface MessageListener {

        public void handleMessage(Mensaje message);
    }
    
}
