
fun main() {

    val str = readln()
    val allowed = readln().split(" ").map{it.toIntOrNull()}.filterNotNull().filter{ it in 0..9 }.map{'0' + it}.toSet()
    println(str.filter { it in allowed })
}
