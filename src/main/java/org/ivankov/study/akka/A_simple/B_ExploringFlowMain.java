package org.ivankov.study.akka.A_simple;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * @author Alexandr Ivankov on 2021-09-27
 */
public class B_ExploringFlowMain {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actor2");

        //1 Filter
        Source<Integer, NotUsed> source = Source.range(1, 200);
        Flow<Integer, Integer, NotUsed> filterFlow = Flow.of(Integer.class).filter(next -> next % 17 == 0);
        Sink<Integer, CompletionStage<Done>> printSink = Sink.foreach(System.out::println);

//        source.via(filterFlow).to(printSink).run(actorSystem);

        //2 MapConcat
        //MapConcat(akka) - flatMap(java)
        Flow<Integer, Integer, NotUsed> mapConcatFlow = Flow.of(Integer.class).mapConcat(next -> List.of(next, next + 1, next + 2));

//        source.via(filterFlow).via(mapConcatFlow).to(printSink).run(actorSystem);

        //3 Group
        Flow<Integer, List<Integer>, NotUsed> groupedFlow = Flow.of(Integer.class)
                .grouped(3);
        Sink<List<Integer>, CompletionStage<Done>> printListSink = Sink.foreach(System.out::println);

//        source.via(filterFlow).via(mapConcatFlow).via(groupedFlow).to(printListSink).run(actorSystem);

        //4 Generic flows - options"
        //4.1 Wrapper class
        //Flow<IntegerList, Integer, NotUsed> ungroupFlow = Flow.of(Integer.class).....
        //4.2 untyped List
//        Flow<List, Integer, NotUsed> ungroupFlow = Flow.of(List.class).mapConcat(next -> next);
        //in the single flow group + ungroup
        Flow<Integer, Integer, NotUsed> groupedAndUngroupFlow = Flow.of(Integer.class)
                .grouped(3)
                .map(next -> {
                    List<Integer> newList = new ArrayList<>(next);
                    newList.sort(Comparator.reverseOrder());
                    return newList;
                })
                .mapConcat(next -> next);
//        source.via(filterFlow).via(mapConcatFlow).via(groupedAndUngroupFlow).to(printSink).run(actorSystem);

        //5 Concat flow
        Flow<Integer, Integer, NotUsed> combinedFlow = mapConcatFlow.via(filterFlow).via(groupedAndUngroupFlow);
        source.via(combinedFlow).to(printSink).run(actorSystem);
    }
}
