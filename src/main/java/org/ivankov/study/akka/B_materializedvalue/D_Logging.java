package org.ivankov.study.akka.B_materializedvalue;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Random;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-10-11
 */
public class D_Logging {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");

        Random random = new Random();
        Source<Integer, NotUsed> source = Source.range(1, 10);

//        Flow<Integer, Integer, NotUsed> flow = Flow.of(Integer.class)
//                .log("flowInput") //old actor system. Required application.conf file
//                .map(x -> x * 2)
//                .log("flowOutput");

        Flow<Integer, Integer, NotUsed> flow = Flow.of(Integer.class)
                .map(x -> {
                    actorSystem.log().debug("flow input " + x);
                    int y = x * 2;
                    actorSystem.log().debug("flow output " + y);
                    return y;
                });

        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        source.via(flow).to(sink).run(actorSystem);

    }
}
