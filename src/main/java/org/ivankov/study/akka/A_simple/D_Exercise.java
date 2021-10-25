package org.ivankov.study.akka.A_simple;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-09-29
 */
public class D_Exercise {

    private final static int SIZE = 10;

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actor4");
        Source<String, NotUsed> source = Source.repeat("").limit(SIZE);

        Flow<String, BigInteger, NotUsed> randomBigInt = Flow.of(String.class).map(next -> new BigInteger(2000, new Random()));
        Flow<BigInteger, BigInteger, NotUsed> nextPrime = Flow.of(BigInteger.class)
                .map(next -> {
                    BigInteger prime = next.nextProbablePrime();
                    System.out.println("next prime:" + next);
                    return prime;
                });

        Flow<BigInteger, List<BigInteger>, NotUsed> groupAndSortFlow = Flow.of(BigInteger.class)
                .grouped(SIZE)
                .map(list -> {
                    List<BigInteger> sorted = new ArrayList<>(list);
                    Collections.sort(sorted);
                    return sorted;
                });

        Sink<List<BigInteger>, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        source.via(randomBigInt).via(nextPrime).via(groupAndSortFlow).to(sink).run(actorSystem);

    }

}
