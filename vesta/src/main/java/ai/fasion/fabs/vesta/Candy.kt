package ai.fasion.fabs.vesta

import org.hashids.Hashids

annotation class NoArg

object Utils {
    private val hashids: Hashids = Hashids(Secret.Salt)

    @JvmStatic
    fun hashId(num: Long): String = hashids.encode(num)
}