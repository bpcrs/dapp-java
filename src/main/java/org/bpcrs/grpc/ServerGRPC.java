package org.bpcrs.grpc;

import com.google.protobuf.GeneratedMessageV3;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.log4j.BasicConfigurator;
import org.bpcrs.hepler.HFHelper;
import org.bpcrs.services.QueryService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class ServerGRPC {

    private Server server;
    private static final Logger logger = Logger.getLogger(ServerGRPC.class.getName());

    private void start() throws Exception {
        int port = Integer.parseInt(System.getProperty("grpc.port"));
        logger.info("Starting the gRPC server");
        server = ServerBuilder.forPort(port)
                .addService(new QueryService())
                .build()
                .start();

        logger.info("Server started. Listening on grpc://localhost:" + port);

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
