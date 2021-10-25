package org.ivankov.study.akka.A_simple;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * @author Alexandr Ivankov on 2021-09-27
 */
public class A_SimpleMain {

    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actor1");

//        RunnableGraph<NotUsed> graph = getIntegerGraph(); //Integer finite example
//        RunnableGraph<NotUsed> graph = getStringGraph(); //String finite example
//        RunnableGraph<NotUsed> graph = getInfinitiveRepeatGraph(); //String Infinitive example
//        RunnableGraph<NotUsed> graph = getInfinitiveCycleGraph(); //String Infinitive cycling list example
//        RunnableGraph<NotUsed> graph = getInfinitiveIteratorGraph(); //String Infinitive stream iterator example
//        RunnableGraph<NotUsed> graph = getInfinitiveSlowedThrottleGraph(); //String Infinitive stream iterator Throttle example
//        RunnableGraph<NotUsed> graph = getInfinitiveSlowedThrottleLimitedGraph(); //String Infinitive limited example
        RunnableGraph<NotUsed> graph = getSinkIgnoreGraph(); //String Ignore  sink example


        graph.run(actorSystem);
        //infinitive running
    }

    private static RunnableGraph<NotUsed> getSinkIgnoreGraph() {
        Iterator<Integer> infiniteRange = Stream.iterate(0, i -> i + 1).iterator();
        Source<Integer, NotUsed> stringSource = Source
                .fromIterator(() -> infiniteRange)
                .throttle(2, Duration.ofSeconds(3))
                .limit(15); // *********************************
        Flow<Integer, String, NotUsed> stringFlow = Flow.of(Integer.class).map(next -> "The next stream limit  value is: " + next);

        Sink<String, CompletionStage<Done>> ignoreSink = Sink.ignore();

        RunnableGraph<NotUsed> streamGraph = stringSource.via(stringFlow).to(ignoreSink);
        return streamGraph;
    }

    private static RunnableGraph<NotUsed> getInfinitiveSlowedThrottleLimitedGraph() {
        Iterator<Integer> infiniteRange = Stream.iterate(0, i -> i + 1).iterator();
        Source<Integer, NotUsed> stringSource = Source
                .fromIterator(() -> infiniteRange)
                .throttle(2, Duration.ofSeconds(3))
                .limit(15); // *********************************
        Flow<Integer, String, NotUsed> stringFlow = Flow.of(Integer.class).map(next -> "The next stream limit  value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> streamGraph = stringSource.via(stringFlow).to(sink);
        return streamGraph;
    }

    private static RunnableGraph<NotUsed> getInfinitiveSlowedThrottleGraph() {
        Iterator<Integer> infiniteRange = Stream.iterate(0, i -> i + 1).iterator();
        Source<Integer, NotUsed> stringSource = Source
                .fromIterator(() -> infiniteRange)
                .throttle(2, Duration.ofSeconds(3));// *********************************
        Flow<Integer, String, NotUsed> stringFlow = Flow.of(Integer.class).map(next -> "The next stream throttle value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> streamGraph = stringSource.via(stringFlow).to(sink);
        return streamGraph;
    }

    private static RunnableGraph<NotUsed> getInfinitiveIteratorGraph() {
        Iterator<Integer> infiniteRange = Stream.iterate(0, i -> i + 1).iterator();
        Source<Integer, NotUsed> stringSource = Source.fromIterator(() -> infiniteRange);// *********************************
        Flow<Integer, String, NotUsed> stringFlow = Flow.of(Integer.class).map(next -> "The next stream value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> streamGraph = stringSource.via(stringFlow).to(sink);
        return streamGraph;
    }

    private static RunnableGraph<NotUsed> getInfinitiveCycleGraph() {
        List<String> names = List.of("Alex", "Ivan", "Albert");
        Source<String, NotUsed> stringSource = Source.cycle(names::iterator);// *********************************
        Flow<String, String, NotUsed> stringFlow = Flow.of(String.class).map(next -> "The next cycle value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> stringGraph = stringSource.via(stringFlow).to(sink);
        return stringGraph;
    }

    private static RunnableGraph<NotUsed> getInfinitiveRepeatGraph() {
        Source<Double, NotUsed> piSource = Source.repeat(3.1415926);// *********************************
        Flow<Double, String, NotUsed> piFlow = Flow.of(Double.class).map(next -> "The next value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> piGraph = piSource.via(piFlow).to(sink);
        return piGraph;
    }

    private static RunnableGraph<NotUsed> getStringGraph() {
        List<String> list2 = List.of("Alex", "Ivan", "Albert");
        Source<String, NotUsed> stringSource = Source.from(list2);// *********************************
        Flow<String, String, NotUsed> stringFlow = Flow.of(String.class).map(next -> "The next value is: " + next);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> stringGraph = stringSource.via(stringFlow).to(sink);
        return stringGraph;
    }

    private static RunnableGraph<NotUsed> getIntegerGraph() {
//        Source<Integer, NotUsed> source = Source.single(111);
//        Source<Integer, NotUsed> source = Source.range(1, 10);
//        Source<Integer, NotUsed> source = Source.range(1, 10, 2);
        List<Integer> list = List.of(1, 2, 3, 76, 3, 41, 635, 654, 234);
        Source<Integer, NotUsed> source = Source.from(list);

        Flow<Integer, String, NotUsed> flow = Flow.of(Integer.class).map(next -> "The next value is: " + next);
        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> graph = source.via(flow).to(sink);

        return graph;
    }
}
