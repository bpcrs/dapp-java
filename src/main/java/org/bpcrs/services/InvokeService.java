package org.bpcrs.services;

import generated.InvokeProto;
import generated.InvokeServiceGrpc;
import generated.QueryProto;
import io.grpc.stub.StreamObserver;
import org.bpcrs.hepler.HFHelper;
import org.bpcrs.object.RequestType;
import org.bpcrs.object.ResultChaincode;

public class InvokeService extends InvokeServiceGrpc.InvokeServiceImplBase {
    /**
     * <pre>
     * Sends a greeting
     * </pre>
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void sendInvoke(InvokeProto.InvokeRequest request, StreamObserver<InvokeProto.InvokeResponse> responseObserver) {
        InvokeProto.InvokeResponse invokeResponse = null;
        try {
            String[] args = request.getArgsList().toArray(new String[0]);
            ResultChaincode resultChaincode = HFHelper.processRequest(RequestType.INVOKE,request.getUsername(), request.getChannel(),
                    request.getChaincode(), request.getFuncName(), args);
            invokeResponse = InvokeProto.InvokeResponse.newBuilder()
                    .setMessage(resultChaincode.getMessage()).setData(resultChaincode.getData().toString()).setCode(resultChaincode.getCode())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onNext(invokeResponse);
        responseObserver.onCompleted();
    }
}
