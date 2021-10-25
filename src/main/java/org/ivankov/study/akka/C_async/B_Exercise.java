package org.ivankov.study.akka.C_async;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-10-11
 */
public class B_Exercise {


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actor4");
        Source<Integer, NotUsed> source = Source.range(1, 10);

        Flow<Integer, BigInteger, NotUsed> randomBigInt = Flow.of(Integer.class)
                .map(next -> {
                    BigInteger result = new BigInteger(3000, new Random());
                    System.out.println("Next BigInteger - " + result);
                    return result;
                });

        //Sync
//        Flow<BigInteger, BigInteger, NotUsed> nextPrime = Flow.of(BigInteger.class)
//                .map(next -> {
//                    BigInteger prime = next.nextProbablePrime();
//                    System.out.println("next prime:" + next);
//                    return prime;
//                });
//

        //Async
        Flow<BigInteger, BigInteger, NotUsed> nextPrime = Flow.of(BigInteger.class)
//                .mapAsync(4, next -> {
                .mapAsyncUnordered(4, next -> {
                    CompletableFuture<BigInteger> future = new CompletableFuture<>();
                    future.completeAsync(() -> {
                        BigInteger prime = next.nextProbablePrime();
                        System.out.println("next prime:" + next);
                        return prime;
                    });
                    return future;
                });

        Flow<BigInteger, List<BigInteger>, NotUsed> groupAndSortFlow = Flow.of(BigInteger.class)
                .grouped(10)
                .map(list -> {
                    List<BigInteger> sorted = new ArrayList<>(list);
                    Collections.sort(sorted);
                    return sorted;
                });

        Sink<List<BigInteger>, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        CompletionStage<Done> run = source
                .via(randomBigInt)
//                .buffer(16, OverflowStrategy.backpressure())
                .async()
//                .via(nextPrime.addAttributes(Attributes.inputBuffer(16, 32)))
                .via(nextPrime)
                .async()
                .via(groupAndSortFlow)
                .toMat(sink, Keep.right())
                .run(actorSystem);
        run.whenComplete((a, thr) -> {
            long finish = System.currentTimeMillis();
            System.out.println("Running for: " + (finish - start));
            actorSystem.terminate();
        });
    }
}
