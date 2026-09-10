package com.kingslegacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class UnitStack(val name: String, var count: Int, val attack: Int, val hp: Int)
data class Hero(var name: String, var level: Int, var gold: Int, var x: Int, var y: Int, val army: MutableList<UnitStack>)

enum class Screen { WORLD, BATTLE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KingsLegacyApp() }
    }
}

@Composable
fun KingsLegacyApp() {
    var screen by remember { mutableStateOf(Screen.WORLD) }
    var hero by remember {
        mutableStateOf(
            Hero("Арен", 1, 500, 1, 1,
                mutableListOf(UnitStack("Мечники", 12, 5, 20),
                    UnitStack("Лучники", 8, 4, 12)))
        )
    }
    var enemyHp by remember { mutableIntStateOf(80) }
    var battleLog by remember { mutableStateOf("Враг преграждает дорогу.") }

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color(0xFF211B16))) {
            when (screen) {
                Screen.WORLD -> WorldScreen(hero) {
                    hero = hero.copy(x = it.first, y = it.second)
                    if (it.first == 4 && it.second == 3) {
                        enemyHp = 80
                        battleLog = "Бой начался!"
                        screen = Screen.BATTLE
                    }
                }
                Screen.BATTLE -> BattleScreen(hero, enemyHp, battleLog,
                    onAttack = {
                        enemyHp = (enemyHp - 18).coerceAtLeast(0)
                        battleLog = if (enemyHp == 0) {
                            hero.gold += 150
                            "Победа! Получено 150 золота."
                        } else "Ваш отряд наносит 18 урона."
                    },
                    onBack = { screen = Screen.WORLD }
                )
            }
        }
    }
}

@Composable
fun WorldScreen(hero: Hero, move: (Pair<Int, Int>) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("KINGS LEGACY", color = Color(0xFFE4C17A), fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Глава I — Королевство Пепла", color = Color.LightGray)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxSize()) {
            Canvas(Modifier.weight(1f).fillMaxHeight()) {
                val cols = 8
                val rows = 5
                val cw = size.width / cols
                val rh = size.height / rows
                for (y in 0 until rows) for (x in 0 until cols) {
                    drawRect(
                        color = if ((x+y)%2==0) Color(0xFF496447) else Color(0xFF3E583D),
                        topLeft = Offset(x*cw, y*rh),
                        size = androidx.compose.ui.geometry.Size(cw-2, rh-2)
                    )
                }
                drawCircle(Color(0xFFDFC36A), radius = minOf(cw,rh)*0.28f,
                    center = Offset((hero.x+.5f)*cw, (hero.y+.5f)*rh))
                drawCircle(Color(0xFF9A342E), radius = minOf(cw,rh)*0.28f,
                    center = Offset(4.5f*cw, 3.5f*rh))
            }
            Column(Modifier.width(210.dp).padding(start = 14.dp)) {
                Text("Герой: ${hero.name}", color = Color.White, fontSize = 18.sp)
                Text("Уровень: ${hero.level}", color = Color.LightGray)
                Text("Золото: ${hero.gold}", color = Color(0xFFE4C17A))
                Spacer(Modifier.height(10.dp))
                Text("Армия", color = Color(0xFFE4C17A), fontWeight = FontWeight.Bold)
                hero.army.forEach { Text("${it.name}: ${it.count}", color = Color.White) }
                Spacer(Modifier.height(12.dp))
                Text("Нажимай на соседнюю клетку, чтобы двигаться.", color = Color.LightGray, fontSize = 12.sp)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            listOf(Pair(0,-1),Pair(-1,0),Pair(1,0),Pair(0,1)).forEach { d ->
                Button(onClick = {
                    val nx=(hero.x+d.first).coerceIn(0,7)
                    val ny=(hero.y+d.second).coerceIn(0,4)
                    move(nx to ny)
                }, modifier=Modifier.padding(3.dp)) { Text("•") }
            }
        }
    }
}

@Composable
fun BattleScreen(hero: Hero, enemyHp: Int, log: String, onAttack: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("ПОШИНОВЫЙ БОЙ", color = Color(0xFFE4C17A), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Враг: $enemyHp HP", color = Color(0xFFE07A6E), fontSize = 18.sp)
        }
        Spacer(Modifier.height(10.dp))
        Canvas(Modifier.fillMaxWidth().weight(1f)) {
            val cols=10; val rows=6; val cw=size.width/cols; val rh=size.height/rows
            for(y in 0 until rows) for(x in 0 until cols)
                drawRect(if((x+y)%2==0) Color(0xFF725E4A) else Color(0xFF665440),
                    Offset(x*cw,y*rh), androidx.compose.ui.geometry.Size(cw-2,rh-2))
            drawCircle(Color(0xFFDFC36A), minOf(cw,rh)*.28f, Offset(2.5f*cw,3f*rh))
            drawCircle(Color(0xFF9A342E), minOf(cw,rh)*.28f, Offset(7.5f*cw,3f*rh))
        }
        Text(log, color=Color.White, modifier=Modifier.padding(6.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Button(onClick=onAttack, enabled=enemyHp>0) { Text(if(enemyHp>0) "Атаковать" else "Победа") }
            OutlinedButton(onClick=onBack) { Text("На карту") }
        }
    }
}
