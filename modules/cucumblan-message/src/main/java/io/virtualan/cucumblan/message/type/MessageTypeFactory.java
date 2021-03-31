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
 * @author Elan Thangmani
 */
public interface MessageTypeFactory {
    /**
     * Build message message type.
     *
     * @param key   the key
     * @param value the value
     * @return the message type
     */
    MessageType buildMessage(Object record, String key, Object value) throws MessageNotDefinedException;

}
