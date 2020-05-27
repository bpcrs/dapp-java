package org.bpcrs.services;

import generated.QueryProto;
import generated.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;

public class QueryService extends QueryServiceGrpc.QueryServiceImplBase {

    public QueryService() {
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void sendQuery(QueryProto.QueryRequest request, StreamObserver<QueryProto.QueryResponse> responseObserver) {
        QueryProto.QueryResponse queryResponse = QueryProto.QueryResponse.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(queryResponse);
        responseObserver.onCompleted();
    }
}
