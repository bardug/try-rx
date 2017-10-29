package rx

import java.util.Random

class Random {

    companion object Generator {

        val rand: java.util.Random = Random()

        fun generate() = rand.nextInt()
    }
}