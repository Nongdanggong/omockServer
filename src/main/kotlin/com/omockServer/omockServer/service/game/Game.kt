package com.omockServer.omockServer.service.game

import com.omockServer.omockServer.converter.dto.Player
import com.omockServer.omockServer.service.User
import kotlin.random.Random

const val BOARD_SIZE = 19

fun generateRandomNumber(): Int = Random.nextInt(0, 2)

class Game(
    val gameId: Int,
    val firstUser: User,
    val secondUser: User,
) {
    // 흑, 백 정함
    val firstPlayer: Player =
        Player(
            user = firstUser,
            stoneColor = if (generateRandomNumber() == 0) StoneColor.BLACK else StoneColor.WHITE,
        )

    val secondPlayer: Player =
        Player(
            user = secondUser,
            stoneColor = if (firstPlayer.stoneColor == StoneColor.BLACK) StoneColor.WHITE else StoneColor.BLACK,
        )

    // 보드 판 초기화
    val board: Array<Array<Int>> =
        Array(BOARD_SIZE) {
            Array(BOARD_SIZE) {
                0
            }
        }

    var turn: StoneColor = StoneColor.BLACK

    fun getPlayerById(playerId: Int): Player = if (firstPlayer.user.userId == playerId) firstPlayer else secondPlayer

    fun changeTurn() {
        turn = if (turn == StoneColor.BLACK) StoneColor.WHITE else StoneColor.BLACK
    }

    fun getNowTurnPlayerId(): Int = if (firstPlayer.stoneColor == turn) firstPlayer.user.userId else secondPlayer.user.userId

    fun placeStone(
        x: Int,
        y: Int,
        playerId: Int,
    ) {
        // 유효하지 않은 x, y 값
        if (!(((0 <= x) and (x < BOARD_SIZE)) and ((0 <= y) and (y < BOARD_SIZE)))) throw Exception("바둑판값전달하셈")
        // 이미 돌이 있는 곳을 전달
        if (board[x][y] != 0) throw Exception("돌이미있는곳임")

        val player = getPlayerById(playerId)

        // 본인 턴이 아닌데 요청
        if (turn != player.stoneColor) throw Exception("니턴아님")

        board[x][y] = player.stoneColor.value

        changeTurn()
    }

    fun checkWin(
        x: Int,
        y: Int,
    ): Boolean {
        val player = board[x][y]

        // 확인할 방향: 가로, 세로, 대각선(\), 대각선(/)
        val directions =
            listOf(
                1 to 0, // 가로
                0 to 1, // 세로
                1 to 1, // 대각선 (\)
                1 to -1, // 대각선 (/)
            )

        for ((dx, dy) in directions) {
            // 현재 놓은 돌을 포함하여 양쪽 방향으로 같은 색 돌의 개수를 셉니다.
            val count =
                countStones(x, y, dx, dy, player) + // 정방향
                    countStones(x, y, -dx, -dy, player) + // 반대방향
                    1 // 현재 위치의 돌

            // 한 줄에 5개 이상이면 승리입니다.
            if (count >= 5) {
                return true
            }
        }

        return false
    }

    private fun countStones(
        x: Int,
        y: Int,
        dx: Int,
        dy: Int,
        player: Int,
    ): Int {
        var count = 0
        var nx = x + dx
        var ny = y + dy

        // 오목판 경계 안에서 같은 색 돌이 있는 동안 반복합니다.
        while ((nx in 0 until BOARD_SIZE) && (ny in 0 until BOARD_SIZE) && (board[nx][ny] == player)) {
            count++
            nx += dx
            ny += dy
        }
        return count
    }

    fun placeStoneAndCheckWin(
        playerId: Int,
        x: Int,
        y: Int,
    ): Boolean {
        placeStone(x = x, y = y, playerId = playerId)
        val isWin = checkWin(x = x, y = y)

        return isWin
    }
}

fun testBlackWinScenarios() {
    println("## 흑돌 승리 시나리오 테스트 시작 ##\n")

    // 1. 흑돌 가로 승리
    run {
        println("--- 1. 흑돌 가로(―) 승리 테스트 ---")
        val game = Game(1, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(9, 7, blackId)
        game.placeStone(10, 7, whiteId)
        game.placeStone(9, 8, blackId)
        game.placeStone(10, 8, whiteId)
        game.placeStone(9, 9, blackId)
        game.placeStone(10, 9, whiteId)
        game.placeStone(9, 10, blackId)
        game.placeStone(10, 10, whiteId)
        val isWin = game.placeStoneAndCheckWin(blackId, 9, 11) // 흑돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 2. 흑돌 세로 승리
    run {
        println("--- 2. 흑돌 세로(|) 승리 테스트 ---")
        val game = Game(2, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(7, 9, blackId)
        game.placeStone(7, 10, whiteId)
        game.placeStone(8, 9, blackId)
        game.placeStone(8, 10, whiteId)
        game.placeStone(9, 9, blackId)
        game.placeStone(9, 10, whiteId)
        game.placeStone(10, 9, blackId)
        game.placeStone(10, 10, whiteId)
        val isWin = game.placeStoneAndCheckWin(blackId, 11, 9) // 흑돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 3. 흑돌 우하향 대각선 승리
    run {
        println("--- 3. 흑돌 우하향 대각선(\\) 승리 테스트 ---")
        val game = Game(3, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(7, 7, blackId)
        game.placeStone(7, 8, whiteId)
        game.placeStone(8, 8, blackId)
        game.placeStone(8, 9, whiteId)
        game.placeStone(9, 9, blackId)
        game.placeStone(9, 10, whiteId)
        game.placeStone(10, 10, blackId)
        game.placeStone(10, 11, whiteId)
        val isWin = game.placeStoneAndCheckWin(blackId, 11, 11) // 흑돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 4. 흑돌 우상향 대각선 승리
    run {
        println("--- 4. 흑돌 우상향 대각선(/) 승리 테스트 ---")
        val game = Game(4, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(11, 7, blackId)
        game.placeStone(11, 6, whiteId)
        game.placeStone(10, 8, blackId)
        game.placeStone(10, 7, whiteId)
        game.placeStone(9, 9, blackId)
        game.placeStone(9, 8, whiteId)
        game.placeStone(8, 10, blackId)
        game.placeStone(8, 9, whiteId)
        val isWin = game.placeStoneAndCheckWin(blackId, 7, 11) // 흑돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }
    println("## 흑돌 승리 시나리오 테스트 종료 ##\n" + "=".repeat(40) + "\n")
}

/**
 * 백돌 승리 시나리오 테스트
 */
fun testWhiteWinScenarios() {
    println("## 백돌 승리 시나리오 테스트 시작 ##\n")

    // 1. 백돌 가로 승리
    run {
        println("--- 1. 백돌 가로(―) 승리 테스트 ---")
        val game = Game(5, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(1, 1, blackId) // 흑돌 첫 수
        game.placeStone(9, 7, whiteId)
        game.placeStone(2, 2, blackId)
        game.placeStone(9, 8, whiteId)
        game.placeStone(3, 3, blackId)
        game.placeStone(9, 9, whiteId)
        game.placeStone(4, 4, blackId)
        game.placeStone(9, 10, whiteId)
        game.placeStone(5, 5, blackId)
        val isWin = game.placeStoneAndCheckWin(whiteId, 9, 11) // 백돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 2. 백돌 세로 승리
    run {
        println("--- 2. 백돌 세로(|) 승리 테스트 ---")
        val game = Game(6, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(1, 1, blackId)
        game.placeStone(7, 9, whiteId)
        game.placeStone(2, 2, blackId)
        game.placeStone(8, 9, whiteId)
        game.placeStone(3, 3, blackId)
        game.placeStone(9, 9, whiteId)
        game.placeStone(4, 4, blackId)
        game.placeStone(10, 9, whiteId)
        game.placeStone(5, 5, blackId)
        val isWin = game.placeStoneAndCheckWin(whiteId, 11, 9) // 백돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 3. 백돌 우하향 대각선 승리
    run {
        println("--- 3. 백돌 우하향 대각선(\\) 승리 테스트 ---")
        val game = Game(7, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(1, 1, blackId)
        game.placeStone(7, 7, whiteId)
        game.placeStone(2, 2, blackId)
        game.placeStone(8, 8, whiteId)
        game.placeStone(3, 3, blackId)
        game.placeStone(9, 9, whiteId)
        game.placeStone(4, 4, blackId)
        game.placeStone(10, 10, whiteId)
        game.placeStone(5, 5, blackId)
        val isWin = game.placeStoneAndCheckWin(whiteId, 11, 11) // 백돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    // 4. 백돌 우상향 대각선 승리
    run {
        println("--- 4. 백돌 우상향 대각선(/) 승리 테스트 ---")
        val game = Game(8, User(0), User(1))
        val (blackId, whiteId) = getPlayerIds(game)

        game.placeStone(1, 1, blackId)
        game.placeStone(11, 7, whiteId)
        game.placeStone(2, 2, blackId)
        game.placeStone(10, 8, whiteId)
        game.placeStone(3, 3, blackId)
        game.placeStone(9, 9, whiteId)
        game.placeStone(4, 4, blackId)
        game.placeStone(8, 10, whiteId)
        game.placeStone(5, 5, blackId)
        val isWin = game.placeStoneAndCheckWin(whiteId, 7, 11) // 백돌 5번째, 승리!
        println("예상 결과: true, 실제 결과: $isWin \n")
    }

    println("## 백돌 승리 시나리오 테스트 종료 ##")
}

/**
 * 게임 인스턴스에서 흑돌과 백돌 플레이어의 ID를 반환하는 헬퍼 함수
 */
fun getPlayerIds(game: Game): Pair<Int, Int> {
    val blackPlayerId = if (game.firstPlayer.stoneColor == StoneColor.BLACK) game.firstUser.userId else game.secondUser.userId
    val whitePlayerId = if (blackPlayerId == game.firstUser.userId) game.secondUser.userId else game.firstUser.userId
    return Pair(blackPlayerId, whitePlayerId)
}

fun main() {
    testBlackWinScenarios()
    testWhiteWinScenarios()
}
