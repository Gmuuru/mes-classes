/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

/**
 *
 * @author rrrt3491
 */
public class MessageEvent implements Event {
    
    public static final int SUCCESS = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    
    private final int type;
    private final String message;

    public MessageEvent(int type, String message){
        this.type = type;
        this.message = message;
    }
    
    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
    
    public static MessageEvent success(String message){
        return new MessageEvent(MessageEvent.SUCCESS, message);
    }
    
    public static MessageEvent warning(String message){
        return new MessageEvent(MessageEvent.WARNING, message);
    }
    
    public static MessageEvent error(String message){
        return new MessageEvent(MessageEvent.ERROR, message);
    }
    
    @Override
    public String toString(){
        return "Message ('"+message+"')";
    }
}
