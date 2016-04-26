/*
package com.bzz.chat.dispatch;

import com.bzz.chat.services.RoomsService;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by TOXA on 4/21/2016.
 *//*

public class ActionDispatcher {

    public Map<String, ServiceMethod> serviceMethodMap = new HashMap<>();

    {
        serviceMethodMap.put("startChat", (Object params) -> startChat(params));
        serviceMethodMap.put("endChat", (Object params) -> endChat(params));
    }


    public Object startChat(Object params){
        */
/*RoomsService service = RoomsService.createProxy(vertx,
                "room-service-address");

        // Save some data in the database - this time using the proxy
        service.save("mycollection", new JsonObject().put("name", "tim"), res2 -> {
          if (res2.succeeded()) {
            // done
          }
        });      *//*

        return null;
    }

    public Object endChat(Object params){
        return null;
    }



    public Object dispatch(String methodName, Object params) {
        final ServiceMethod serviceMethod = serviceMethodMap.get(methodName);
        if (serviceMethod != null) {
            return serviceMethod.execute(params);
        }
        return null;
    }
}
*/
