package com.bzz.chat.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by TOXA on 4/23/2016.
 */
public class RoomsServiceImpl implements RoomsService {

    private Vertx vertx;

    public RoomsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void createRoom(String roomId, JsonObject document, Handler<AsyncResult<JsonObject>> resultHandler) {
        System.out.println("Processing createRoom...");
        try {
            //create room if not exists and add clients to that room
            //TODO fix it
            vertx.sharedData().getLocalMap("chat.room." + roomId).put("test", "test");
            JsonObject result = new JsonObject();//document.copy();
            result.put("status", "ok");
            //TODO fix it
            JsonObject data = new JsonObject("{}");
            result.put("data", data);
            resultHandler.handle(Future.succeededFuture(result));
        } catch (Exception e) {
            JsonObject result = new JsonObject();
            //TODO fix it
            result.put("status", "fail");
            JsonObject errorData = new JsonObject("{ \"error\", \"" + e.getMessage() + "\"}");
            result.put("data", errorData);
            resultHandler.handle(Future.succeededFuture(result));
        }
    }
}
