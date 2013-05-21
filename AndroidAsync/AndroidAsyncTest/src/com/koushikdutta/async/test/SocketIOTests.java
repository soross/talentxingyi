package com.koushikdutta.async.test;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.async.future.SimpleFuture;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.SocketIOClient;
import com.koushikdutta.async.http.SocketIOClient.EventCallback;
import com.koushikdutta.async.http.SocketIOClient.JSONCallback;
import com.koushikdutta.async.http.SocketIOClient.SocketIOConnectCallback;
import com.koushikdutta.async.http.SocketIOClient.StringCallback;

public class SocketIOTests extends TestCase {
    public static final long TIMEOUT = 100000L;
    
    
    class TriggerFuture extends SimpleFuture<Boolean> {
        public void trigger(boolean val) {
            setComplete(val);
        }
    }
    
//    public void testChannels() throws Exception {
//        final TriggerFuture trigger = new TriggerFuture();
//        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://koush.clockworkmod.com/chat", new SocketIOConnectCallback() {
//            @Override
//            public void onConnectCompleted(Exception ex, SocketIOClient client) {
//                assertNull(ex);
//                client.setStringCallback(new StringCallback() {
//                    @Override
//                    public void onString(String string) {
//                        trigger.trigger("hello".equals(string));
//                    }
//                });
//                client.emit("hello");
//            }
//        });
//        assertTrue(trigger.get(TIMEOUT, TimeUnit.MILLISECONDS));
//
//    }
    
    public void testEchoServer() throws Exception {
        final TriggerFuture trigger1 = new TriggerFuture();
        final TriggerFuture trigger2 = new TriggerFuture();
        final TriggerFuture trigger3 = new TriggerFuture();

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://koush.clockworkmod.com:8080", new SocketIOConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                assertNull(ex);
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string) {
                        trigger1.trigger("hello".equals(string));
                    }
                });
                client.setEventCallback(new EventCallback() {
                    @Override
                    public void onEvent(String event, JSONArray arguments) {
                        trigger2.trigger(arguments.length() == 3);
                    }
                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json) {
                        trigger3.trigger("world".equals(json.optString("hello")));
                    }
                });
                try {
                    client.emit("hello");
                    client.emit(new JSONObject("{\"hello\":\"world\"}"));
                    client.emit("ping", new JSONArray("[2,3,4]"));
                }
                catch (JSONException e) {
                }
            }
        });

        assertTrue(trigger1.get(TIMEOUT, TimeUnit.MILLISECONDS));
        assertTrue(trigger2.get(TIMEOUT, TimeUnit.MILLISECONDS));
        assertTrue(trigger3.get(TIMEOUT, TimeUnit.MILLISECONDS));
    }

//    public void testReconnect() throws Exception {
//        final TriggerFuture trigger = new TriggerFuture();
//
//
//        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://koush.clockworkmod.com:8080", new SocketIOConnectCallback() {
//            @Override
//            public void onConnectCompleted(Exception ex, final SocketIOClient oldClient) {
//                assertNull(ex);
//                oldClient.disconnect();
//                oldClient.reconnect(new SocketIOConnectCallback() {
//                    @Override
//                    public void onConnectCompleted(Exception ex, SocketIOClient client) {
//                        assertNull(ex);
//                        assertEquals(client, oldClient);
//                        client.setStringCallback(new StringCallback() {
//                            @Override
//                            public void onString(String string) {
//                                trigger.trigger("hello".equals(string));
//                            }
//                        });
//                        client.emit("hello");
//                    }
//                });
//            }
//        });
//
//        assertTrue(trigger.get(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
}
