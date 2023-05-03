import kotlin.random.Random

fun main() {
    initializeMineSweeper()
}

class MineField(var numOfMines: Int) {
    private val mineLoc = mutableListOf<Mine>()
    private val randomGen = Random.Default
    var failed = false
    private var firstFree = true
    private val mineField = MutableList(9) { MutableList(9) {"."} }

    fun mine(x: Int, y: Int) {
        when {
            Mine(y, x) in mineLoc -> {
                numOfMines--
                if (mineField[y][x] == ".") {
                    mineField[y][x] = "*"
                } else if (mineField[y][x] == "*") {
                    numOfMines++
                    mineField[y][x] = "."
                } else {
                    mineField[y][x] = "*"
                }
            }
            else -> {
                if (mineField[y][x] == ".") {
                    mineField[y][x] = "*"
                } else if (mineField[y][x] == "*") {
                    mineField[y][x] = "."
                } else {
                    mineField[y][x] = "*"
                }
            }
        }
        displayField()
    }

    fun free(x: Int, y: Int) {
        if (firstFree) {
            if (Mine(y, x) in mineLoc) shiftMine(x, y)
            firstFree = false
        }

        when {
            Mine(y, x) in mineLoc -> {
                println("You stepped on a mine and failed!")
                for (m in mineLoc) {
                    mineField[m.y][m.x] = "X"
                }
                failed = true
            }
            else -> {
                if (mineField[y][x] == "/") {
                    mineField[y][x] = "."
                } else {
                    showCells(mineField, x, y)
                }
            }
        }
        displayField()
    }

    private fun shiftMine(row: Int, column:Int) {
        var x: Int
        var y: Int
        do {
            x = randomGen.nextInt(0, 9)
            y = randomGen.nextInt(0, 9)
        } while (Mine(y, x) in mineLoc)
        mineLoc.remove(Mine(column, row))
        mineLoc.add(Mine(y, x))
    }

    fun generateMines() {
        repeat(numOfMines) {
            var x = randomGen.nextInt(0, 9)
            var y = randomGen.nextInt(0, 9)
            while (Mine(y, x) in mineLoc) {
                x = randomGen.nextInt(0, 9)
                y = randomGen.nextInt(0, 9)
            }
            mineLoc.add(Mine(y, x))
        }
    }

    private fun checkForMines(row: Int, column: Int): String {
        var count = 0
        for (y in row - 1..row + 1) {
            if (y in 0..8) {
                for (x in column - 1..column + 1) {
                    if (x in 0..8) {
                        if (Mine(x, y) in mineLoc) {
                            count++
                        }
                    }
                }
            }
        }
        return if (count > 0) count.toString() else "/"
    }

    private fun showSurroundingCell(screen: MutableList<MutableList<String>>, x: Int, y: Int, oldCord: String) {
        if (x !in 0..8 || y !in 0..8) return
        if (screen[y][x] != oldCord && screen[y][x] != "*") return
        if (Mine(y, x) in mineLoc) return
        for (row in x - 1..x + 1) {
            if (row !in 0..8) {
                for (column in y - 1..y + 1) {
                    if (column !in 0..8)
                        if (Mine(column, row) in mineLoc) {
                            mineField[y][x] = checkForMines(x, y)
                            return
                        }
                }
            }
        }


        mineField[y][x] = checkForMines(x, y)
        showSurroundingCell(screen, x - 1, y, oldCord)
        showSurroundingCell(screen, x + 1, y, oldCord)
        showSurroundingCell(screen, x, y - 1, oldCord)
        showSurroundingCell(screen, x, y + 1, oldCord)
    }

    private fun showCells(screen: MutableList<MutableList<String>>, x: Int, y: Int) {
        val oldCord = screen[x][y]
        if (oldCord == "/") return
        showSurroundingCell(screen, x, y, oldCord)
    }

    fun displayField() {
        println("""
             │123456789│
            —│—————————│
         """.trimIndent())
        for (row in mineField.indices) {
            print("${row + 1}│")
            print(mineField[row].joinToString(""))
            println("│")
        }
        println("—│—————————│")
        println()
    }
}

fun initializeMineSweeper() {
    val mineField: MineField
    var numOfMines: Int
    do {
        print("How many mines do you want on the field? > ")
        val input = readln().toInt()
        numOfMines = when {
            input < 1 -> {
                println("Number Of Mines Can't be less than 1")
                0
            }
            input > 80 -> {
                println("Too many mines")
                0
            }
            else -> {
                input
            }
        }
    } while (numOfMines == 0)
    mineField = MineField(numOfMines)
    mineField.displayField()
    mineField.generateMines()

    do {
        print("Set/unset mine marks or claim a cell as free: > ")
        val (x, y, dec) = readln().split(" ")
        val row = x.toInt() - 1
        val column = y.toInt() - 1
        when (dec) {
            "mine" -> mineField.mine(row, column)
            "free" -> mineField.free(row, column)
        }
    } while (mineField.numOfMines > 0 && !mineField.failed)
    if (mineField.numOfMines == 0) {
        println("Congratulations! You found all the mines!")
    }
}

data class Mine(val y: Int, val x: Int)