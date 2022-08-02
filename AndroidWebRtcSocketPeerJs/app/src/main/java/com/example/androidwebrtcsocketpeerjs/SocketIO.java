package com.example.androidwebrtcsocketpeerjs;



import android.util.Log;



import org.json.JSONObject;


import java.net.URISyntaxException;


import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import io.socket.engineio.client.Transport;

public class SocketIO {
    private  static  String  SERVER_PATH="http://192.168.1.15:4000";
    private  static Socket mSocket=null;


    private static Emitter.Listener receiveSocketId = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Socket recu"+args[0]);

            mSocket.emit("receive_socket_userId",1);
        }
    };







    public static  Socket  connectToSocketIo(){

        if(mSocket==null){
            try {

                System.out.println("Nous testons le socket ici");
                mSocket = IO.socket(SERVER_PATH).connect();
                mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Transport transport = (Transport) args[0];
                        transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Exception e = (Exception) args[0];
                                Log.e("Socket error", "Transport error " + e.getMessage());
                                e.printStackTrace();
                                e.getCause().printStackTrace();
                            }
                        });
                    }
                });






            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        }


        mSocket.on("exchange_socket_id",receiveSocketId);

        return  mSocket;


    }








}
