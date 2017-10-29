package rx

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class TimeTicker(private val interval: Long) {

    val observed: Subject<Long> = BehaviorSubject.create<Long>()

    private var tickerThread: Thread = newTickerThread()

    private @Volatile
    var paused: Boolean = false

    fun start() {

        if (!tickerThread.isAlive) {
            println("Creating a new ticker thread")
            tickerThread = newTickerThread()
        }

        try {
            println("TimeTicker starts now")
            tickerThread.start()

        } catch (e: Exception) {
            observed.onError(RuntimeException("Unexpected Error", e))
        }

    }

    private fun newTickerThread(): Thread {
        return Thread({
            while (!Thread.currentThread().isInterrupted) {

                try {
                    Thread.sleep(interval)
                } catch (e: InterruptedException) {
                    break
                }

                if (paused) {
                    continue
                }
                println("$observed Emitting")
                observed.onNext(System.currentTimeMillis())
            }
            println("Interrupted. Propagating onComplete")
            observed.onComplete()
        })
    }

    fun stop() {
        if (!tickerThread.isAlive) {
            return
        }
        println("Interrupting")
        tickerThread.interrupt()
        println("Joining")
        tickerThread.join()
        println("Stopped")
    }
}