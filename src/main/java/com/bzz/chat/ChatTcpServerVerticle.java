package com.bzz.chat;

import com.bzz.chat.dispatch.ServiceMethod;
import com.bzz.chat.services.RoomsService;
import com.bzz.chat.services.RoomsServiceImpl;
import com.bzz.chat.util.Runner;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.serviceproxy.ProxyHelper;

import java.text.DateFormat;
import java.time.Instant;
import java.util.*;


/**
 * Created by TOXA on 4/16/2016.
 */
public class ChatTcpServerVerticle extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runExample(ChatTcpServerVerticle.class);
    }


    private NetServer server;
    private ArrayList<NetSocket> clients;
    private HashSet<String> clientIds = new HashSet<>();
    /*private HashMap<String, ArrayList<String>> rooms;*/

    //final ActionDispatcher dispatcher = new ActionDispatcher();

    RoomsService service;

    final RecordParser parser = RecordParser.newDelimited("\n", h -> {
        System.out.println(h.toString());
    });

    @Override
    public void start() throws Exception {
        server = vertx.createNetServer();
        register(vertx);
        service = RoomsService.createProxy(vertx, "room-service-address");
        //we'll store rooms here
        //vertx.sharedData().getLocalMap("chat.room." + chatRoom);

        //handle incoming connection
        server.connectHandler(netSocket -> {

            // Composing a client address string
            SocketAddress addr = netSocket.remoteAddress();
            String addrString = addr.host() + addr.port();
            System.out.println("Incoming connection! Addr: " + addrString);

            netSocket.write("Welcome to the chat " + addrString + "!");


            EventBus eb = vertx.eventBus();

            eb.consumer("broadcast_address", message -> {
                System.out.println("Broadcasting message: " + message.body());
                netSocket.write(String.valueOf(message.body()));
            });

            //client id, we can use it to send messages
            clientIds.add(netSocket.writeHandlerID());

            //process incoming message
            netSocket.handler(inBuffer -> {
                JsonObject json = inBuffer.toJsonObject();

                dispatch(json.getString("action"), json.getJsonObject("data"),
                        res -> {
                            if (res.succeeded()) {
                                JsonObject response = res.result();
                                netSocket.write(response.encode());
                                System.out.println("dispatch success, response: " + response);
                            } else {
                                netSocket.write(res.result().encode());
                                System.out.println("dispatch action failed, error: " + res.cause());
                            }
                        });


                //chat functionality
                /*String data =
                        inBuffer.getString(0, inBuffer.length());

                // Create a timestamp string
                String timestamp = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date.from(Instant.now()));
                //System.out.println(addrString + "<" + timestamp + ">" + " incoming data: " + data);

                Buffer outBuffer = Buffer.buffer();
                outBuffer.appendString(addrString + "<" + timestamp + ">: " + data);

                eb.publish("broadcast_address", outBuffer);*/
                // end chat functionality

            });
        });
        server.listen(10000, result -> {
            System.out.println("Server started on port 10000");
        });

    }


    public Map<String, ServiceMethod> serviceMethodMap = new HashMap<>();

    {
        serviceMethodMap.put("create", (Object params, Handler<AsyncResult<JsonObject>> resultHandler) -> startChat(params, resultHandler));
        serviceMethodMap.put("endChat", (Object params, Handler<AsyncResult<JsonObject>> resultHandler) -> endChat(params, resultHandler));
    }


    public void startChat(Object params, Handler<AsyncResult<JsonObject>> resultHandler) {


        service.createRoom("test", (JsonObject) params, res2 -> {
            if (res2.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res2.result()));
                System.out.println("create room handler ok");// done
            } else if (res2.failed()) {
                resultHandler.handle(Future.failedFuture(res2.cause()));
                System.out.println("create room handler failed, error: " + res2.cause());
            }
        });
        // Save some data in the database - this time using the proxy
        /*service.save("mycollection", new JsonObject().put("name", "tim"), res2 -> {
          if (res2.succeeded()) {
            // done
          }
        });*/
        //return null;
    }

    public Object endChat(Object params, Handler<AsyncResult<JsonObject>> resultHandler) {
        return null;
    }


    public void dispatch(String methodName, Object params, Handler<AsyncResult<JsonObject>> resultHandler) {
        final ServiceMethod serviceMethod = serviceMethodMap.get(methodName);
        if (serviceMethod != null) {
            serviceMethod.execute(params, resultHandler);
        } else {
            System.out.println("Dispatcher didn't find corresponding method, params: " + params);
        }
    }


    public void register(Vertx vertx) {
        // Create an instance of your service implementation
        RoomsService service = new RoomsServiceImpl(vertx);
        // Register the handler
        ProxyHelper.registerService(RoomsService.class, vertx, service,
                "room-service-address");
    }

    public void unregister(Vertx vertx) {
        // Create an instance of your service implementation
        RoomsService service = new RoomsServiceImpl(vertx);
        // Register the handler
        MessageConsumer<JsonObject> consumer = ProxyHelper.registerService(RoomsService.class, vertx, service,
                "room-service-address");
        // Unregister your service.
        ProxyHelper.unregisterService(consumer);
    }

    public void proxyCreation(Vertx vertx, DeliveryOptions options) {
        RoomsService service = ProxyHelper.createProxy(RoomsService.class,
                vertx,
                "room-service-address");
        // or with delivery options:
        RoomsService service2 = ProxyHelper.createProxy(RoomsService.class,
                vertx,
                "database-service-address", options);
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close(result -> {
            if (result.succeeded()) {
                System.out.println("TCP server stopped");
            }
        });
    }
}