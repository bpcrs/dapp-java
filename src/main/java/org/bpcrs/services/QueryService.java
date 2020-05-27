package org.bpcrs.services;

import generated.QueryProto;
import generated.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.bpcrs.hepler.HFHelper;

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

        QueryProto.QueryResponse queryResponse = null;
        try {
            queryResponse = QueryProto.QueryResponse.newBuilder()
                    .setMessage(HFHelper.queryChaincode("hungpt","mychannel","fabcar","queryAllCars")).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onNext(queryResponse);
        responseObserver.onCompleted();
    }
}
