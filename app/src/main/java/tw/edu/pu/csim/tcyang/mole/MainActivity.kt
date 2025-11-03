package tw.edu.pu.csim.tcyang.mole

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import kotlin.random.Random
import kotlin.math.roundToInt

class MoleViewModel : ViewModel() {

    // 遊戲常數
    private val GAME_DURATION = 60 // 遊戲時間 (秒)

    // ** 遊戲狀態 **
    var counter by mutableLongStateOf(0)  // 分數
        private set
    var stay by mutableIntStateOf(GAME_DURATION) // 剩餘時間 (秒)
        private set
    var isGameOver by mutableStateOf(false) // 遊戲是否結束
        private set

    // ** 地鼠位置狀態 **
    var offsetX by mutableIntStateOf(0) // 地鼠 X 軸偏移量 (像素)
        private set
    var offsetY by mutableIntStateOf(0) // 地鼠 Y 軸偏移量 (像素)
        private set

    // ** 遊戲區域資訊 **
    private var maxWidth = 1 // 螢幕寬度 (像素)
    private var maxHeight = 1 // 螢幕高度 (像素)
    private var moleSize = 1 // 地鼠尺寸 (像素)


    // 1. 處理點擊事件
    fun incrementCounter() {
        if (!isGameOver) {
            counter++
            moveMole() // 點擊後立即移動地鼠
        }
    }

    // 2. 獲取遊戲區域尺寸 (由 UI 呼叫)
    fun getArea(intSize: IntSize, moleSizePx: Int) {
        maxWidth = intSize.width
        maxHeight = intSize.height
        moleSize = moleSizePx
        // 首次獲取尺寸後，將地鼠移動到一個初始位置
        if (offsetX == 0 && offsetY == 0) {
            moveMole()
        }
    }

    // 3. 隨機移動地鼠 (核心遊戲邏輯)
    fun moveMole() {
        if (!isGameOver) {
            // 計算地鼠可移動的範圍：從 0 到 (螢幕尺寸 - 地鼠尺寸)
            val newX = Random.nextInt(0, maxWidth - moleSize)

            // 限制地鼠在螢幕下半部活動，避開頂部的文字區域 (從 20% 高度開始)
            val minY = (maxHeight * 0.2).roundToInt()
            val newY = Random.nextInt(minY, maxHeight - moleSize)

            offsetX = newX
            offsetY = newY
        }
    }

    // 4. 處理遊戲倒計時
    fun tickTime() {
        if (stay > 0) {
            stay--
        } else {
            isGameOver = true // 時間到，遊戲結束
        }
    }

    // 5. 重啟遊戲
    fun restartGame() {
        counter = 0
        stay = GAME_DURATION
        isGameOver = false
        moveMole()
    }
}