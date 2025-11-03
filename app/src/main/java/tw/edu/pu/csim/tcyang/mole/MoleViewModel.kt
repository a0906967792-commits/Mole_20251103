package tw.edu.pu.csim.tcyang.mole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tw.edu.pu.csim.tcyang.mole.ui.theme.MoleTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoleTheme {
                // 請確保您的 drawable 資料夾中有名為 'mole' 的圖片資源
                MoleScreen()
            }
        }
    }
}

@Composable
fun MoleScreen(moleViewModel: MoleViewModel = viewModel()) {
    // 從 ViewModel 獲取所有狀態
    val counter = moleViewModel.counter
    val stay = moleViewModel.stay
    val isGameOver = moleViewModel.isGameOver

    // DP-to-pixel 轉換
    val density = LocalDensity.current
    val moleSizeDp = 150.dp
    val moleSizePx = with(density) { moleSizeDp.roundToPx() }


    // --- 遊戲邏輯控制：計時器與地鼠移動 ---

    // 計時器：每秒更新一次時間
    LaunchedEffect(isGameOver) {
        // 只有在遊戲未結束且時間大於 0 時才執行
        if (!isGameOver) {
            while (moleViewModel.stay > 0) {
                delay(1000) // 延遲 1 秒
                moleViewModel.tickTime()
            }
        }
    }

    // 地鼠隨機移動：每 800 毫秒移動一次
    LaunchedEffect(isGameOver, stay) {
        if (!isGameOver) {
            while (moleViewModel.stay > 0) {
                delay(800) // 延遲 0.8 秒
                moleViewModel.moveMole()
            }
        }
    }


    // --- UI 佈局 ---
    Box (
        modifier = Modifier.fillMaxSize()
            .onSizeChanged { intSize ->
                // 獲取全螢幕尺寸並傳給 ViewModel，以計算地鼠移動範圍
                moleViewModel.getArea(intSize, moleSizePx)
            },
        contentAlignment = Alignment.TopCenter
    ) {
        // 頂部文字區域：標題、分數與時間
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            // 標題 (置中)
            Text(
                text = "打地鼠遊戲(鄭姿佳)",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // 分數與時間 (置中)
            Text(
                text = "分數: $counter \n時間: $stay",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = if (stay <= 10 && !isGameOver) Color.Red else Color.Black // 低於 10 秒變紅
            )
        }

        // 遊戲結束提示 (居中於螢幕中央)
        if (isGameOver) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "遊戲結束！",
                    fontSize = 40.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "最終分數: $counter",
                    fontSize = 32.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                // 重玩按鈕
                Button(onClick = { moleViewModel.restartGame() }) {
                    Text("重新開始", fontSize = 24.sp)
                }
            }
        }
    }

    // 地鼠圖片 Composable
    // 只有在遊戲未結束時才顯示地鼠
    if (!isGameOver) {
        Image(
            painter = painterResource(id = R.drawable.mole),
            contentDescription = "地鼠",
            modifier = Modifier
                // 使用 ViewModel 提供的動態位置
                .offset { IntOffset(moleViewModel.offsetX, moleViewModel.offsetY) }
                .size(moleSizeDp)
                .clickable { moleViewModel.incrementCounter() } // 點擊處理 (會自動停止加分)
        )
    }
}