package com.koushikdutta.async.test;

import android.os.Environment;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.*;
import com.koushikdutta.async.http.AsyncHttpClient.StringCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

public class MultipartTests extends TestCase {
    AsyncHttpServer httpServer;
    AsyncServer server;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new AsyncServer();
        server.setAutostart(true);

        httpServer = new AsyncHttpServer();
        httpServer.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                fail();
            }
        });
        httpServer.listen(server, 5000);
        
        httpServer.post("/", new HttpServerRequestCallback() {
            int gotten = 0;
            @Override
            public void onRequest(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                final MultipartFormDataBody body = (MultipartFormDataBody)request.getBody();
                body.setMultipartCallback(new MultipartCallback() {
                    @Override
                    public void onPart(Part part) {
                        if (part.isFile()) {
                            body.setDataCallback(new DataCallback() {
                                @Override
                                public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                    gotten += bb.remaining();
                                    bb.clear();
                                }
                            });
                        }
                    }
                });

                request.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        response.send(body.getField("baz") + gotten + body.getField("foo"));
                    }
                });
            }
        });
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        httpServer.stop();
        server.stop();
    }

    public void testUpload() throws Exception {
        File dummy = new File(Environment.getExternalStorageDirectory(), "AndroidAsync/dummy.txt");
        final String FIELD_VAL = "bar";
        dummy.getParentFile().mkdirs();
        FileOutputStream fout = new FileOutputStream(dummy);
        byte[] zeroes = new byte[100000];
        for (int i = 0; i < 10; i++) {
            fout.write(zeroes);
        }
        fout.close();
//        StreamUtility.writeFile(dummy, DUMMY_VAL);
        
        AsyncHttpPost post = new AsyncHttpPost("http://localhost:5000");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("foo", FIELD_VAL);
        body.addFilePart("my-file", dummy);
        body.addStringPart("baz", FIELD_VAL);
        post.setBody(body);

        Future<String> ret = AsyncHttpClient.getDefaultInstance().execute(post, new StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
            }
        });
        
        String data = ret.get(500000, TimeUnit.MILLISECONDS);
        assertEquals(data, FIELD_VAL + 1000000 + FIELD_VAL);
    }
}
