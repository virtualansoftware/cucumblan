package io.virtualan.cucumblan.message.exception;
/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
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

/**
 * The type Message not defined exception.
 */
public class SkipMessageException extends  Exception{

  /**
   * Instantiates a new Message not defined exception.
   */
  public SkipMessageException(){
    super();
  }

  /**
   * Instantiates a new Message not defined exception.
   *
   * @param msg the msg
   */
  public SkipMessageException(String msg){
    super(msg);
  }

}
