package edu.rit.se.sars.communication.ipc;

import edu.rit.se.sars.communication.message.MessagePublisher;
import edu.rit.se.sars.communication.message.internal.event.DroneEventDeserializer;
import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.serde.MessageSerializer;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SocketClient<S,D> extends ProtoQueuedClient<S,D> {

    private final BlockingDeque<byte[]> messageQueue = new LinkedBlockingDeque<>();
    private PrintWriter out;
    private BufferedReader in;

    public SocketClient(
            InetSocketAddress serverAddress,
            MessageSerializer<S> serializer,
            MessageDeserializer<D> deserializer,
            MessagePublisher<D> publisher
    ) {
        super(serverAddress, serializer, deserializer, publisher);
    }

    @Override
    public void socketSend(byte[] message) {
        messageQueue.add(message);
    }

    @Override
    public void socketConnect(InetSocketAddress addr) {
        try {
            Socket clientSocket = new Socket(addr.getAddress(),addr.getPort());
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e ) {
            logger.debug("Failed to connect to drone server.");
            e.printStackTrace();
            return;
        }
        new Thread(() -> {
            try {
                broadcastQueue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private byte[] formatStringToByteArray (String literalBytes) {
        if (literalBytes == null || literalBytes.length() < 2) {
            return new byte[0];
        }
        String[] tokens = literalBytes.substring(1,literalBytes.length() - 1).split(",");
        byte[] bytes = new byte[tokens.length];
        for (int i = 0 ; i < tokens.length ; i ++ ) {
            bytes[i] = Byte.decode(tokens[i].trim());
        }
        return bytes;
    }

    @Override
    public byte[] blockingReceive() {
        try {
            return formatStringToByteArray(in.readLine());
        } catch (Exception e) {
            logger.debug("Failed to receive message from drone.");
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void broadcastQueue() throws InterruptedException {
        while (true) {
            byte[] outgoingMessage = messageQueue.take();
            //Intentionally doing the wrong call here, since Java can't into bytes.
            //For byte array [10,-112,100,0], literally returns "[10, -112, 100, 0]\n"
            //Requires manual trim, split on ',', trim each token, read to BYTE.  I know.
            //Feel free to fix.

            //Originally had new String(outgoingMessage, StandardCharsets.UTF_8) + "\n" (the newline for the line reader on the socket)
            //But this kept converting into something longer and malformed.  I suck at Java when it's low level.
            out.println(Arrays.toString(outgoingMessage) + "\n");
        }
    }
}
