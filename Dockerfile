# mvn package ## generate fat jar using maven shade plugin
# java -cp target/libraryManagement-1.0.0-SNAPSHOT-fat.jar com.diep.libraryManagement.verticle.MainVerticle ## run locally
# sudo docker build -t lib-man .
# sudo docker run --rm --name api -p 88:8888 lib-man

FROM openjdk:17-oracle

ENV FAT_JAR libraryManagement-1.0.0-SNAPSHOT-fat.jar
WORKDIR /app

COPY ./target/$FAT_JAR /app

EXPOSE 8888

ENTRYPOINT ["sh", "-c"]

CMD ["exec java -cp $FAT_JAR com.diep.libraryManagement.verticle.MainVerticle"]
