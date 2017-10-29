package rx

import io.reactivex.Observable
import io.reactivex.Observable.fromIterable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.FutureTask
import java.util.stream.Collectors
import java.util.stream.IntStream

fun main(args: Array<String>) {
    val intsObservable = fromIterable(intIterableRange())

    intsObservable.subscribe({ i ->
        println("${Thread.currentThread().name} hello $i")
    })

    println("--------------------------------------------------------")

    val futureTask = FutureTask({ intIterable() })
    val futureObservable = Observable.fromFuture(futureTask)
    Schedulers.newThread().scheduleDirect({ futureTask.run() })

    futureObservable
            .subscribeOn(Schedulers.io())
            .subscribe(
                    /* onNext */ basicPrintFunction(),
                    /* onError */ basicErrorPrintFunction(),
                    /* onComplete */ printDoneFunction())

    Thread.sleep(100)

}

fun printDoneFunction(): () -> Unit {
    return {
        println("Completed")
    }
}

fun basicErrorPrintFunction(): (Throwable) -> Unit = { t -> println("Boom: ${t.stackTrace}") }

private fun basicPrintFunction(): (Iterable<Int>) -> Unit {
    return { list ->
        list
                .filter { i -> i > 0 }
                .forEach(
                        { i ->
                            println("${Thread.currentThread().name} future hello $i")
                        })
    }
}

fun intIterable(): Iterable<Int> {
    return IntStream.generate({ Random.generate() })
            .limit(2000)
            .boxed()
            .collect(Collectors.toList())
}

fun intIterableRange(): Iterable<Int> {
    return IntStream.range(100, 700)
            .limit(2000)
            .boxed()
            .collect(Collectors.toList())
}