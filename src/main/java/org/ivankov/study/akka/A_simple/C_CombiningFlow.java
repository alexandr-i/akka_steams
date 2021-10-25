package org.ivankov.study.akka.A_simple;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-09-29
 */
public class C_CombiningFlow {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actor3");

        Source<String, NotUsed> source = Source.from(List.of("My dear wife",
                "Lovely sun",
                "Great mountains"));

        Flow<String, Integer, NotUsed> flow = Flow.of(String.class)
                .map(sentence -> sentence.split(" ").length);

        Source<Integer, NotUsed> sizeSource = source.via(flow);

        Sink<Integer, CompletionStage<Done>> printSink = Sink.foreach(System.out::println);


    }
}
