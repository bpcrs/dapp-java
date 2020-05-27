package org.bpcrs.services;

import generated.QueryProto;
import generated.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.bpcrs.hepler.HFHelper;
import org.bpcrs.object.RequestType;
import org.bpcrs.object.ResultChaincode;

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
            String[] args = request.getArgsList().toArray(new String[0]);
            ResultChaincode resultChaincode = HFHelper.processRequest(RequestType.QUERY,request.getUsername(), request.getChannel(),
                    request.getChaincode(), request.getFuncName(), args);
            queryResponse = QueryProto.QueryResponse.newBuilder()
                    .setMessage(resultChaincode.getMessage()).setData(resultChaincode.getData().toString()).setCode(resultChaincode.getCode())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onNext(queryResponse);
        responseObserver.onCompleted();
    }
}
