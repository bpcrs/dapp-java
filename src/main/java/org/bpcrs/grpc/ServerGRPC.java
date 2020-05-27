package org.bpcrs.grpc;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.log4j.BasicConfigurator;
import org.bpcrs.services.QueryService;

import java.util.Arrays;
import java.util.logging.Logger;

public class ServerGRPC {

    private int port = 42420;
    private Server server;
    private static final Logger logger = Logger.getLogger(ServerGRPC.class.getName());

    private void start() throws Exception {
        logger.info("Starting the grpc server");
        server = ServerBuilder.forPort(port)
                .addService(new QueryService())
                .build()
                .start();

        logger.info("Server started. Listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** JVM is shutting down. Turning off grpc server as well ***");
            ServerGRPC.this.stop();
            System.err.println("*** shutdown complete ***");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        logger.info("Server startup. Args = " + Arrays.toString(args));
        final ServerGRPC serverGRPC = new ServerGRPC();
        serverGRPC.start();
        serverGRPC.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
