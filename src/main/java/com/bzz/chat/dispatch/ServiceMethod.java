package com.bzz.chat.dispatch;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * Created by TOXA on 4/21/2016.
 */
@FunctionalInterface
public interface ServiceMethod {
    void execute(Object params, Handler<AsyncResult<JsonObject>> resultHandler);
}