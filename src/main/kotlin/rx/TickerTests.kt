package rx

import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val timeTicker = TimeTicker(100)
    timeTicker.start()

    println("Observing ${timeTicker.observed}")
    timeTicker.observed
            .sample(50, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.newThread())
            .subscribe({ time ->
                println("${Thread.currentThread().name} tick $time")
            },
                    basicErrorPrintFunction(),
                    printDoneFunction())

    timeTicker.observed
            .observeOn(Schedulers.newThread())
            .subscribe({ time ->
                println("${Thread.currentThread().name} tack $time")
            })

    timeTicker.observed
            .observeOn(Schedulers.io())
            .subscribe({ time ->
                println("${Thread.currentThread().name} toe $time")
            })

    Thread.sleep(5000)
    timeTicker.stop()
}