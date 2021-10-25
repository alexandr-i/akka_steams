package org.ivankov.study.akka.B_materializedvalue;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Random;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-10-11
 */
public class A_CounterFold {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");
        ActorSystem actorSystem2 = ActorSystem.create(Behaviors.empty(), "actorSystem");

        Random random = new Random();
        Source<Integer, NotUsed> source = Source.range(1, 100).map(x -> random.nextInt(1000) + 1);

        Flow<Integer, Integer, NotUsed> filterGT200 = Flow.of(Integer.class).filter(x -> x > 200);
        Flow<Integer, Integer, NotUsed> filterEven = Flow.of(Integer.class).filter(x -> x % 2 == 0);

        Sink<Integer, CompletionStage<Done>> oldSink = Sink.foreach(System.out::println);
        Sink<Integer, CompletionStage<Integer>> mvSink = Sink.fold(0, (counter, value) -> {
            System.out.println(value);
            return counter + 1;
        });

        CompletionStage<Integer> result = source
                .via(filterGT200)
                .via(filterEven)
                .toMat(mvSink, Keep.right())
                .run(actorSystem);

        result.whenComplete((value, thr) -> {
            if (thr != null) {
                System.out.println("You've got an error");
            } else {
                System.out.println("Finished. Materialized value is " + value);
            }
            actorSystem.terminate();
        });

        //Terminate with no MAT
        CompletionStage<Done> result2 = source.toMat(Sink.ignore(), Keep.right()).run(actorSystem2);
        result2.whenComplete((value, thr) -> actorSystem2.terminate());
    }
}
