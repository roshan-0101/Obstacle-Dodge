package com.example.obstacledodge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlin.math.sqrt
import kotlin.properties.Delegates
import kotlin.random.Random

//size of the screen==w=2200,h=1080
class MyCanvas(context: Context, obsNo: Int) : View(context) {
    private var gamerun: Boolean = true
    private var EndX by Delegates.notNull<Float>()
    private var EndY by Delegates.notNull<Float>()
    private val ground = 50f
    private lateinit var groundRectF: RectF
    private var groundSurface = BitmapFactory.decodeResource(resources, R.drawable.ground)
    private val extrafun = extraFun()
    private val noOfObs = obsNo
    private val bigObsNo = Random.nextInt(2, obsNo / 2)
    private val smallObsNo = obsNo - bigObsNo


    //backcolor
    private val bgcolor = ResourcesCompat.getColor(resources, R.color.skyBlue, null)
    private var backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.background)

    private lateinit var canv1: Canvas
    private lateinit var bitmap: Bitmap

    //obstacle
    private var obstacleRect: Array<RectF> = Array(bigObsNo) { RectF() }
    private var obsImg = BitmapFactory.decodeResource(resources, R.drawable.big_obs)
    private val obstacleSpeed = 10f
    private val obstacleHeight = 200f
    private val obstacleWidth = 80f
    private var dismaintain by Delegates.notNull<Float>()
    private val obstacleColor = ResourcesCompat.getColor(resources, R.color.red, null)
    private val obstacle = Paint().apply {
        color = obstacleColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    //smaller obstacle rectangle
    private val smallObsHeight = 100f
    private val smallObsWidth = 70f
    private val smallObsImage = BitmapFactory.decodeResource(resources, R.drawable.small_obs)
    private val smallObsColor: Paint = Paint().apply {
        color = obstacleColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var smallObstacleRect: Array<RectF> = Array(smallObsNo) { RectF() }

    private val winTape=BitmapFactory.decodeResource(resources, R.drawable.win)
    private lateinit var winShape:RectF
    private val winWidth=20f

    //pause button
    private var pauseImage: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.pause)
    private var pauseEdge = 120f
    private var pauseDis = 60f

    private lateinit var pause: RectF
    private var pausestate = false


    //origin
    private val origin = Paint().apply {
        R.color.black.also { color = it }
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    //player
    private val playerimage = BitmapFactory.decodeResource(resources, R.drawable.player)

    private val playercolor = ContextCompat.getColor(context, R.color.blue)
    private val playerRadius = 100f
    private val player = Paint().apply {
        color = playercolor
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var playerY = playerRadius + ground
    private var playerX = 700f
    private lateinit var playerRect: RectF

    //player jumps
    private var isJumping = false
    private var jumpHeight = 500f
    private var jumpTimeUp: Long = 400
    private var jumpTimeDown: Long = 500

    //chaser
    private val chaserimage = BitmapFactory.decodeResource(resources, R.drawable.chaser)
    private val chaserRadius = playerRadius + 50
    private var chaserY = chaserRadius + ground
    private var chaserPlayerDis = 400f
    private var chaserX = playerX - (playerRadius + chaserPlayerDis)

    private lateinit var chaserRect: RectF

    //chaser jumps
    private var chaserIsJumping = false
    private var chaserJumpHeight = 500f
    private var chaserJumpTimeUp: Long = 350
    private var chaserJumpTimeDown: Long = 500


    private var touchX = 0f
    private var touchY = 0f
    //scores
    private var smallScore=0
    private var bigScore=0
    private var totalScore = 0

    private fun obsArray(small: Int, big: Int): MutableList<Int> {
        var arr = mutableListOf<Int>()
        for (i in 0 until small) {
            arr.add(0)
        }
        for (i in 0 until big) {
            arr.add(1)
        }
        for (i in arr.indices.reversed()) {
            val j = Random.nextInt(i + 1)
            val temp = arr[i]
            arr[i] = arr[j]
            arr[j] = temp
        }
        return arr
    }//0=small,1=big

    private var totalObsArrayPos = obsArray(smallObsNo, bigObsNo)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canv1 = Canvas(bitmap)
        canv1.drawColor(bgcolor)
        EndX = w.toFloat()
        EndY = h.toFloat()
        groundRectF = RectF(0F, EndY - (ground + 1), EndX, EndY)
        playerY = EndY - playerY
        chaserY = EndY - chaserY
        chaserX= playerX-(playerRadius+chaserPlayerDis)

        //obstacleRect =arrayOf(RectF(w.toFloat(), h - (obstacleHeight + ground), left + obstacleWidth, h - ground))
        Log.d("Array", "$totalObsArrayPos ")
        dismaintain = 0f
        var i = 0
        var j = 0


        for (ii in 0 until noOfObs) {
            if (totalObsArrayPos[ii] == 1) {
                obstacleRect[i] = RectF(
                    EndX + dismaintain,
                    EndY - (obstacleHeight + ground),
                    left + dismaintain + obstacleWidth,
                    EndY - ground
                )
                Log.d("bigobs", "bigobs=${obstacleRect[i].left}")

                i++
            } else {
                smallObstacleRect[j] = RectF(
                    EndX + dismaintain,
                    EndY - (smallObsHeight + ground),
                    left + dismaintain + smallObsWidth,
                    EndY - ground
                )
                Log.d("smallobs", "smallobs=${smallObstacleRect[j].left}")

                j++
            }
            dismaintain += Random.nextInt(900, 1400)
        }
        Log.d("size def", "w=$w,h=$h ")
        val u=totalObsArrayPos.last()
        var dis=dismaintain+EndX
        winShape= RectF(dis,0f, (left+winWidth),EndY)
    }


    override fun onDraw(canvas: Canvas?) {
        Log.d("gamerun", "gamerun=$gamerun ")

        Log.d("On draw call", "x=$EndX,y=$EndY ")
        super.onDraw(canvas)
        //canvas?.scale(1f, -1f)
        //canvas?.translate(0f, -height.toFloat())
        canv1.drawBitmap(bitmap, 0f, 0f, null)
        //canvas?.drawBitmap(backgroundImage,0f,0f,null)
        canv1.drawColor(bgcolor)
        //player
        //canvas?.drawCircle(playerX, playerY, playerRadius, player)
        //chaser
        //canvas?.drawCircle(chaserX, chaserY, chaserRadius, chaser)
        playerRect = RectF(
            playerX - playerRadius,
            playerY - playerRadius,
            playerX + playerRadius,
            playerY + playerRadius
        )

        chaserRect = RectF(
            chaserX - chaserRadius,
            chaserY - chaserRadius,
            chaserX + chaserRadius,
            chaserY + chaserRadius
        )
        canvas?.drawBitmap(chaserimage, null, chaserRect, null)
        canvas?.drawBitmap(playerimage, null, playerRect, null)

        //ground
        canvas?.drawBitmap(groundSurface, null, groundRectF, null)

        //pause button
        pause = RectF(EndX - (pauseDis + pauseEdge), pauseDis, EndX - pauseDis, pauseDis + pauseEdge)
        canvas?.drawBitmap(pauseImage, null, pause, null)
        var i = 0
        var j = 0

        for (ii in 0 until noOfObs) {
            if (totalObsArrayPos[ii] == 0) {//canvas?.drawRect(smallObsRect[i],smallObsColor)
                canvas?.drawBitmap(smallObsImage, null, smallObstacleRect[i], null)
                i++
                Log.d("smallobs", "smallobsdrawn: ")
            }
            else {
                //canvas?.drawRect(obstacleRect[j], obstacle)
                canvas?.drawBitmap(obsImg, null, obstacleRect[j], null)
                j++
                Log.d("Bigobs", "bigpbsdrawn: ")
            }
            Log.d("obs render", "$ii ")
        }
        //reference to origin
        canvas?.drawCircle(0f, 0f, 20f, origin)


        canvas?.drawBitmap(winTape,null,winShape,null)
        if (gamerun ) {
            if(pausestate&&isJumping)
                update()
            if(!pausestate)
                update()
            Log.d("ondrawisjumpupdate", "pauseatate=$pausestate:isjumping=$isJumping ")
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        touchX = event!!.x
        touchY = event.y
        for (obsRect in obstacleRect) {
            Log.d("touch call", " motion X=$touchX, y=$touchY, position=${obsRect.left - playerX}")
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("JUMP", "onTouchEvent ")
                if (pause.contains(touchX, touchY)) {
                    Log.d("paused", "PAUSE HOGAYA $pausestate: ")
                    pauseAction()
                } else {
                    if (!isJumping) {
                        isJumping = true
                        animateJump()
                    }
                }
            }
        }
        return true
    }


    private fun animateJump() {
        Log.d("jumpinitial", "jump is initialised: ")
        var originalY = playerY
        val targetY = EndY - (ground + jumpHeight + playerRadius)
        Log.d("pause Anime", "$pausestate: $originalY ")
        // Perform a ValueAnimator animation to move the path vertically
    val jumpAnimator = ValueAnimator.ofFloat(originalY, targetY).apply {
            duration = jumpTimeUp // Adjust the animation duration as needed
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
            //if(pausestate)return@addUpdateListener//pause the animation
                playerY = valueAnimator.animatedValue as Float
                Log.d("jumpinfo", "obstacle dis=${obstacleRect[0].left} ")
                invalidate()
            }
        }
        val fallAnimator = ValueAnimator.ofFloat(targetY, originalY).apply {
            duration = jumpTimeDown // Adjust the animation duration as needed
            interpolator = AccelerateInterpolator()
            addUpdateListener { valueAnimator ->
            //if(pausestate)return@addUpdateListener//pause

                playerY = valueAnimator.animatedValue as Float
                Log.d("jump stop", "obstacle dis=${obstacleRect[0].left} ")
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {

                        isJumping = false
                        playerY = originalY
                        invalidate()

                }
            })

        }

    jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                fallAnimator.start()
            }
        })
        jumpAnimator.start()
}


    private fun chaserJumpAutomator() {
        for (i in obstacleRect) {
            if (i.left - chaserX in 100f..210f) {
                if (!chaserIsJumping) {
                    chaserIsJumping = true
                    animateChaserJump()
                }
            }
        }
        for (i in smallObstacleRect) {
            if (i.left - chaserX in 100f..210f) {
                if (!chaserIsJumping) {
                    chaserIsJumping = true
                    animateChaserJump()
                }
            }
        }
    }


    private fun animateChaserJump() {
        val originalY = chaserY
        val targetY = EndY - (ground + chaserJumpHeight + chaserRadius)
        Log.d("chASE", "CHASE: ")
        // Perform a ValueAnimator animation to move the path vertically
        val jumpAnimator = ValueAnimator.ofFloat(originalY, targetY).apply {
            duration = chaserJumpTimeUp // Adjust the animation duration as needed
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                chaserY = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        val fallAnimator = ValueAnimator.ofFloat(targetY, originalY).apply {
            duration = chaserJumpTimeDown // Adjust the animation duration as needed
            interpolator = AccelerateInterpolator()
            addUpdateListener { valueAnimator ->
                chaserY = valueAnimator.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    chaserIsJumping = false
                    chaserY = originalY
                    invalidate()
                }
            })

        }
        jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                fallAnimator.start()
            }
        })
        jumpAnimator.start()
    }

    private fun animateChaserApproach() {
        val originalX = chaserX
        val targetX = chaserX+200

        val approachAnimator = ValueAnimator.ofFloat(originalX, targetX).apply {
            duration = 100 // Adjust the animation duration as needed
            interpolator = AccelerateInterpolator()
            addUpdateListener { valueAnimator ->
                chaserX=valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        approachAnimator.start()
    }

    var collisionOccur =  MutableList(smallObstacleRect.size) { false }

    private fun update() {
        for (obsRect in obstacleRect) {
            Log.d(
                "Update_pause",
                "$pausestate"
            )
            obsRect.left -= obstacleSpeed
            obsRect.right = obsRect.left + obstacleWidth
            totalScore=bigScore+smallScore


            if (extrafun.isCollisionDetected(playerX, playerY, playerRadius, obsRect)) {
                // Collision occurred, end the game
                Log.d("collision detect", "collided ")
                // Show game over dialog or perform any other game over logic
                showGameOverDialog()
            }
            if(playerX==obsRect.left)bigScore+=100

            invalidate()
        }
        //scoreManager()
        chaserJumpAutomator()


        for ((i, smallObsRect) in smallObstacleRect.withIndex()) {
            Log.d(
                "small update call",
                "update: left=${smallObsRect.left}, right=${smallObsRect.right} cx=$chaserX,cy=$chaserY"
            )
            smallObsRect.left -= obstacleSpeed
            smallObsRect.right = smallObsRect.left + obstacleWidth

            Log.d("collbool", "${collisionOccur[i]} $i ")


            if (extrafun.isCollisionDetected(playerX, playerY, playerRadius, smallObsRect) && !collisionOccur[i]) {
                // Collision occurred, stumbled the game
                animateChaserApproach()
                collisionOccur[i]=true
                Log.d("small_collision detect", "collided ${collisionOccur[i]}  $i ")
            }
            if(playerX==smallObsRect.left)smallScore+=50

        invalidate()
        }

        chaserWithPlayer()
        winShape.left -= obstacleSpeed
        winShape.right = winShape.left + winWidth
        if(winShape.right<=(playerX-playerRadius)) {showGameOverDialog()}
        invalidate()
    }

    private fun chaserWithPlayer() {
        val x=playerX-chaserX
        val y=playerY-chaserY
        val dis=x*x+y*y
        if (sqrt(dis)<=playerRadius+chaserRadius){showGameOverDialog()}
    }

    private fun pauseAction() {
        pausestate = true
        Log.d("pause fun", "pause fun $pausestate ")

        val dialog: AlertDialog = extrafun.dialogBuilder(R.layout.pause_layout, context)
        val resume: Button = dialog.findViewById(R.id.resume)
        val home: Button = dialog.findViewById(R.id.Home)
        val score: TextView = dialog.findViewById(R.id.score)
        score.text ="Your score=$totalScore"
        resume.setOnClickListener {
            pausestate = false
            dialog.dismiss()
            invalidate()

        }
        home.setOnClickListener {
            returnHome()
            dialog.dismiss()

        }
    }

    private fun showGameOverDialog() {
        Log.d("Game over call", "showGameOverDialog: ")
        gamerun=false
        // Code to show the game over dialog
        // Customize the dialog appearance and behavior according to your requirements
        // For example, create an AlertDialog with a custom layout and set its properties
        val dialog = extrafun.dialogBuilder(R.layout.gameover, context)
        val home: Button = dialog.findViewById(R.id.home)
        val score: TextView = dialog.findViewById(R.id.endDialog)
        totalScore=smallScore+bigScore
        Log.d("totalScore", "total score=$totalScore: ")
        datastore(totalScore)
        score.text ="Score=$totalScore"
        home.setOnClickListener {
            dialog.dismiss()
            returnHome()
        }
    }
    private fun returnHome() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    private fun datastore(score:Int) {

        val sharedPref = context.getSharedPreferences("highscore", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var exist:Int=sharedPref.getInt("bestscore",0)
        if(score>exist){
            editor.putInt("bestscore", score)
            editor.apply()}
    }
    private fun scoreManager(){
        for (i in obstacleRect){
            if(i.left-playerX==0f) {
                bigScore += 100
                Log.d("ScoreBigObs", "big obstacle dodged bugScore=$bigScore: ")
            }

        }
        for (i in smallObstacleRect){
            if(i.left-playerX==0f) {
                smallScore += 50
                Log.d("ScoreSmallObs", "small obstacle dodged smallscore=$smallScore: ")

            }
        }
    }
}
