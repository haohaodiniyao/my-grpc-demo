# grpc和springboot
Maven
protobuf:compile
protobuf:compile-custom


mvn clean package -Dmaven.test.skip=true -U
mvn clean protobuf:compile protobuf:compile-custom
mvn clean protobuf:compile protobuf:compile-custom package -Dmaven.test.skip=true -U

https://github.com/yidongnan/grpc-spring-boot-starter
https://github.com/envoyproxy/protoc-gen-validate