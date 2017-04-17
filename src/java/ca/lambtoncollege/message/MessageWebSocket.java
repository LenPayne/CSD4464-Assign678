/*
 * Copyright 2016 Len Payne <len.payne@lambtoncollege.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.lambtoncollege.message;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@ServerEndpoint("/socket")
@ApplicationScoped
public class MessageWebSocket {

    @Inject
    MessageController messageController;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            String output = "";
            JsonObject json = Json.createReader(new StringReader(message)).readObject();            
            if (json.containsKey("getAll")) {
                output = messageController.getAllJson().toString();
            } else if (json.containsKey("getById")) {
                output = messageController.getByIdJson(json.getInt("getById")).toString();
            } else if (json.containsKey("getFromTo")) {
                JsonArray dates = json.getJsonArray("getFromTo");
                try {
                    Date from = sdf.parse(dates.getString(0));
                    Date to = sdf.parse(dates.getString(1));
                    output = messageController.getByDateJson(from, to).toString();
                } catch (ParseException ex) {
                    output = Json.createObjectBuilder()
                            .add("error", "Error parsing dates: " + dates.toString())
                            .build().toString();
                }
            } else if (json.containsKey("post")) {
                output = messageController.addJson(json.getJsonObject("post")).toString();
            } else if (json.containsKey("put")) {
                int id = json.getJsonObject("put").getInt("id");
                output = messageController.editJson(id, json.getJsonObject("put")).toString();
            } else if (json.containsKey("delete")) {
                output = Json.createObjectBuilder()
                        .add("ok", messageController.deleteById(json.getInt("delete")))
                        .build().toString();
            } else {
                output = Json.createObjectBuilder()
                        .add("error", "Invalid Request")
                        .add("original", json)
                        .build().toString();
            }
            session.getBasicRemote().sendText(output);
        } catch (IOException ex) {
            Logger.getLogger(MessageWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
