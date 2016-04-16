package com.bzz.chat;

import com.bzz.chat.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;


/**
 * Created by TOXA on 4/16/2016.
 */
public class ChatTcpServerVerticle extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
      Runner.runExample(ChatTcpServerVerticle.class);
    }


    private NetServer server;

    @Override
    public void start() throws Exception {
        server = vertx.createNetServer();

        server.connectHandler(netSocket -> {

            // Composing a client address string
            SocketAddress addr = netSocket.remoteAddress();
            String addrString = addr.host() + addr.port();
            System.out.println("Incoming connection! Addr: " + addrString);
            netSocket.write("Welcome to the chat " + addrString  + "!");


            EventBus eb = vertx.eventBus();

            eb.consumer("broadcast_address", message -> {
                System.out.println("Broadcasting message: " + message.body());
                netSocket.write(String.valueOf(message.body()));
            });


            netSocket.handler(inBuffer -> {
                String data =
                        inBuffer.getString(0, inBuffer.length());

                // Create a timestamp string
                String timestamp = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date.from(Instant.now()));
                //System.out.println(addrString + "<" + timestamp + ">" + " incoming data: " + data);

                Buffer outBuffer = Buffer.buffer();
                outBuffer.appendString(addrString + "<" + timestamp + ">: " + data);

                eb.publish("broadcast_address", outBuffer);
            });
        });
        server.listen(10000, result -> {
            System.out.println("Server started on port 10000");
        });

    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close(result -> {
            if (result.succeeded()) {
                //TCP server fully closed
                System.out.println("TCP server fully closed");
            }
        });
    }
}