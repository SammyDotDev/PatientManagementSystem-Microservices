package com.devnaza.billingservice.grpc;

import billing.BillingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase{

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
        StreamObserver<billing.BillingResponse> responseObserver){
        log.info("createBillingAccount {}", billingRequest.toString());

//        Business logic - e.g save to database, perform calculations, e.t.c.

        BillingResponse response = BillingResponse.newBuilder().setAccountId(billingRequest.getPatientId()).setStatus("ACTIVE").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}
