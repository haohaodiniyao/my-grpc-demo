package com.example;

import com.examples.lib.HelloReply;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidationException;
import io.envoyproxy.pgv.ValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidationExceptions;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;


@Slf4j
@GrpcGlobalServerInterceptor
public class GlobalExceptionInterceptor implements ServerInterceptor {
    private final ValidatorIndex index = new ReflectiveValidatorIndex();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(call, metadata)) {

            @Override
            public void onMessage(ReqT message) {
                log.info("请求参数：{}",message.toString());
                try {
                    index.validatorFor(message.getClass()).assertValid(message);
                    super.onMessage(message);

                } catch (ValidationException e) {
                    Status status = ValidationExceptions.asStatus(e);
                    log.error("异常：{}",e);
                    call.sendHeaders(new Metadata());
                    call.sendMessage((RespT) HelloReply.newBuilder().setMessage(e.getMessage()).build());
                    call.close(Status.OK,new Metadata());
//                    call.close(status, new Metadata());
                }

            }
        };
    }
}