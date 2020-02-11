package lispy

fun runRepl(env: Env) {
    print("lisk>")
    System.`in`.bufferedReader().lineSequence().forEach {
        try {
            println(it.parseProgram().eval(env).toCode())
        } catch (e: Exception) {
            System.err.println(e)
            System.err.flush()
        }
        print("lisk>")
    }
}

fun runDefaultRepl() = runRepl(Env(null, mutableMapOf()).also { it.registerBuiltinProcedures() })

fun main(args: Array<String>) {
    runDefaultRepl()
}
