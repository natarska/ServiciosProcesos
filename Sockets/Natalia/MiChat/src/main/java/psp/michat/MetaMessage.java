/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp.michat;

/**
 *
 * @author dirrospace
 */
public class MetaMessage {
    enum TypeMessage {
        ORDEN, MENSAJE;
    }
    
    private Object container;
    private TypeMessage type;

    public MetaMessage() {
    }

    public Object getContainer() {
        return container;
    }

    public void setContainer(Object container) {
        this.container = container;
    }

    public TypeMessage getType() {
        return type;
    }

    public void setType(TypeMessage type) {
        this.type = type;
    }    
}
