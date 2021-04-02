package io.virtualan.cucumblan.message.exception;

/**
 * The type Message not defined exception.
 */
public class MessageNotDefinedException extends  Exception{

  /**
   * Instantiates a new Message not defined exception.
   */
  public MessageNotDefinedException(){
    super();
  }

  /**
   * Instantiates a new Message not defined exception.
   *
   * @param msg the msg
   */
  public MessageNotDefinedException(String msg){
    super(msg);
  }

}
