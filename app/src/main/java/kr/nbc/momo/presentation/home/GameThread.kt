package kr.nbc.momo.presentation.home

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameThread(private val surfaceHolder: SurfaceHolder) : Thread() {
    var running = true
    private val paint = Paint()
    private var playerY = 190f
    private var playerVelocity = 5f
    private val gravity = 1f
    private var lastJump = 0L
    private var hurdleX = 800f
    private var hurdleVelocity = -7f
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    fun jump() {
        if (lastJump < System.currentTimeMillis() - 1200) {
            playerVelocity = -10f
            lastJump = System.currentTimeMillis()
        }
    }

    override fun run() {
        running = true
        _score.value = 0

        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    update()
                    draw(canvas)
                    drawHurdle(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            if (180f < playerY) {
                if (40f < hurdleX && hurdleX < 55f) {
                    running = false
                }
            }

        }
    }

    private fun update() {
        playerVelocity += gravity
        playerY += playerVelocity

        if (playerY > 190f) {
            playerY = 190f
            playerVelocity = 0f
        }

        hurdleX += hurdleVelocity

        if (hurdleX < 0f) {
            hurdleX = 800f
            hurdleVelocity -= 1f
            _score.value += 1
        }
    }

    private fun draw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        paint.color = Color.BLUE
        canvas.drawRect(40f, playerY, 55f, playerY + 15f, paint)
    }

    private fun drawHurdle(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawRect(hurdleX, 190f, hurdleX + 15f, 205f, paint)
    }

}