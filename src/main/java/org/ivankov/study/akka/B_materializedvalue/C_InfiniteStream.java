package org.ivankov.study.akka.B_materializedvalue;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-10-11
 */
public class C_InfiniteStream {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");

        Random random = new Random();
        Source<Integer, NotUsed> source = Source.repeat(1).map(x -> random.nextInt(1000) + 1);

        Flow<Integer, Integer, NotUsed> filterGT200 = Flow.of(Integer.class).filter(x -> x > 200);
        Flow<Integer, Integer, NotUsed> filterEven = Flow.of(Integer.class).filter(x -> x % 2 == 0);

        Sink<Integer, CompletionStage<Integer>> mvSink = Sink.reduce((firstValue, secondValue) -> {
            System.out.println(secondValue);
            return firstValue + secondValue;
        });

        CompletionStage<Integer> result = source.take(100).throttle(1, Duration.ofSeconds(1))
                .takeWithin(Duration.ofSeconds(5)) //work for 5 secs and stop
//                .via(filterGT200.limit(60)) //Exception is here: akka.stream.StreamLimitReachedException: limit of 60 reached
                .via(filterGT200.take(110))
                .via(filterEven.takeWhile(value -> value < 900))
                .toMat(mvSink, Keep.right())
                .run(actorSystem);

        result.whenComplete((value, thr) -> {
            if (thr != null) {
                System.out.println("You've got an error: " + thr);
            } else {
                System.out.println("Finished. Materialized value is " + value);
            }
            actorSystem.terminate();
        });

    }
}
