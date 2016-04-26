package com.bzz.chat.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Created by TOXA on 4/23/2016.
 */

@ProxyGen
public interface RoomsService {

    // A couple of factory methods to create an instance and a proxy
    static RoomsService create(Vertx vertx) {
        return new RoomsServiceImpl(vertx);
    }

    static RoomsService createProxy(Vertx vertx, String address) {
        //return ProxyHelper.createProxy(RoomsService.class, vertx, address);
        // Alternatively, you can create the proxy directly using:
         return new RoomsServiceVertxEBProxy(vertx, address);
        // The name of the class to instantiate is the service interface + `VertxEBProxy`.
        // This class is generated during the compilation
    }

    // Actual service operations here...
    /*void save(String collection, JsonObject document,
       Handler<AsyncResult<Void>> resultHandler);
    }*/

    void createRoom(String roomId, JsonObject document, Handler<AsyncResult<JsonObject>> resultHandler);

   /* public addClientToRoom(String clientId) {

    }

    public deleteRoom(String) {

    }*/






















/*    private static String GLOBAL_OBJECT_PREFIX = "chat.room.";

    public SharedData sd = vertx.sharedData();

    public createRoom(String roomId){

        vertx.sharedData().getSet(GLOBAL_OBJECT_PREFIX + chatRoom).add(id);

    }

    public addClientToRoom(String clientId){

    }

    public deleteRoom(String ){

    }*/
}

