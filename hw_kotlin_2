class Node(var value: Int?) {
    var left: Node? = null
    var right: Node? = null
    fun addValue(inp: Int):Node {
        if (value == null)
            value = inp
        else if (inp < value!!)
        {
            if (left == null)
                left = Node(inp)
            else
                left!!.addValue(inp)
        }
        else
        {
            if (right == null)
                right = Node(inp)
            else
                right!!.addValue(inp)
        }
        return this
    }
    fun printInorder() {
        println("${this.value?:return} ")
        left?.printInorder()
        right?.printInorder()
    }
}

fun main() {

    val array = generateSequence(::readlnOrNull).toList().flatMap{ it.split(" ")}.map{it.toIntOrNull()}.filterNotNull()
    val tree = array.fold(Node(null)){tree, next -> tree.addValue(next)}
    tree.printInorder()
}
