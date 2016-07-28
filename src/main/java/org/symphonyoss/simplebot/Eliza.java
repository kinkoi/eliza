/*
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.simplebot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.pod.model.User;


import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.pod.model.Stream;


import org.symphonyoss.client.services.*;
import org.symphonyoss.client.services.RoomService;;


public class Eliza implements RoomListener {

    private final Logger logger = LoggerFactory.getLogger(Eliza.class);
    private SymphonyClient symClient;
    private Map<String,String> initParams = new HashMap<String,String>();
    private RoomService roomService;
    private Room elizaRoom;


    private static Set<String> initParamNames = new HashSet<String>();
    static {
        initParamNames.add("sessionauth.url");
        initParamNames.add("keyauth.url");
        initParamNames.add("pod.url");
        initParamNames.add("agent.url");
        initParamNames.add("truststore.file");
        initParamNames.add("truststore.password");
        initParamNames.add("keystore.password");
        initParamNames.add("certs.dir");
        initParamNames.add("bot.user.name");
        initParamNames.add("bot.user.email");
        initParamNames.add("room.stream");
    }

    public static void main(String[] args) {
        new Eliza();
        System.exit(0);
    }

    public Eliza() {
        initParams();
        initAuth();
        initRoom();
        sendMessage("Hey there! I'm the Eliza!");
        sendMessage("All done here, bye!");
    }

    private void initParams() {
        for(String initParam : initParamNames) {
            String systemProperty = System.getProperty(initParam);
            if (systemProperty == null) {
                throw new IllegalArgumentException("Cannot find system property; make sure you're using -D" + initParam + " to run Eliza");
            } else {
                initParams.put(initParam,systemProperty);
            }
        }
    }

    private void initAuth() {
        try {
            symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            AuthorizationClient authClient = new AuthorizationClient(
                    initParams.get("sessionauth.url"),
                    initParams.get("keyauth.url"));


            authClient.setKeystores(
                    initParams.get("truststore.file"),
                    initParams.get("truststore.password"),
                    initParams.get("certs.dir") + initParams.get("bot.user.name") + ".p12",
                    initParams.get("keystore.password"));

            SymAuth symAuth = authClient.authenticate();


            symClient.init(
                    symAuth,
                    initParams.get("bot.user.email"),
                    initParams.get("agent.url"),
                    initParams.get("pod.url")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initRoom() {
        Stream stream = new Stream();
        stream.setId(initParams.get("room.stream"));

        try {
         roomService = new RoomService(symClient);

         elizaRoom = new Room();
         elizaRoom.setStream(stream);
         elizaRoom.setId(stream.getId());
         elizaRoom.setRoomListener(this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


    private MessageSubmission getMessage(String message) {
        MessageSubmission aMessage = new MessageSubmission();
        aMessage.setFormat(MessageSubmission.FormatEnum.TEXT);
        aMessage.setMessage(message);
        return aMessage;
    }

    private void sendMessage(String message) {
        MessageSubmission messageSubmission = getMessage(message);
        try {
            symClient.getMessageService().sendMessage(elizaRoom, messageSubmission);
            logger.info("[MESSAGE] - "+message);
            System.out.println("[MESSAGE] - "+message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomMessage(RoomMessage roomMessage) {

        Room room = roomService.getRoom(roomMessage.getId());

        if(room!=null && roomMessage.getMessage() != null)
            logger.debug("New room message detected from room: {} on stream: {} from: {} message: {}",
                    room.getRoomDetail().getRoomAttributes().getName(),
                    roomMessage.getRoomStream().getId(),
                    roomMessage.getMessage().getFromUserId(),
                    roomMessage.getMessage().getMessage()

                );

    }
}
    

