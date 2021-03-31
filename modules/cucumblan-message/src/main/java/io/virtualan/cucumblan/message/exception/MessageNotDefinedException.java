package io.virtualan.cucumblan.message.exception;

public class MessageNotDefinedException extends  Exception{
  public MessageNotDefinedException(){
    super();
  }

  public MessageNotDefinedException(String msg){
    super(msg);
  }

}
