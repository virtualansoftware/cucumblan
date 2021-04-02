package io.virtualan.cucumblan.message.type;

/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */


import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;

/**
 * The interface Message type.
 *
 * @param <T>  the type parameter
 * @param <TT> the type parameter
 * @author Elan Thangmani
 */
public interface  MessageTypeFactory<T, TT> {

  /**
   * Build message message type.
   *
   * @param record the key
   * @param key    the key
   * @param value  the value
   * @return the message type
   * @throws MessageNotDefinedException the message not defined exception
   */
  MessageType buildMessage(Object record, T key, TT value) throws MessageNotDefinedException;

  /**
   * Build message type message type.
   *
   * @param type  the type
   * @param key   the key
   * @param value the value
   * @return the message type
   * @throws MessageNotDefinedException the message not defined exception
   */
  MessageType buildMessageType(String type, T key, TT value) throws MessageNotDefinedException;

}
