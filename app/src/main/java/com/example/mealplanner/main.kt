import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    runBlocking {
        val obj: Any = "hello"
        val str: String? = obj as? String // str is "hello"

        val num: Any = 123
        val str2: String? = num as? String // str2 is null

        val num1: Any = 123
        val str3: String = num as String // str2 is null
    }
}