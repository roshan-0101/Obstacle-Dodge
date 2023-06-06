package com.example.obstacledodge

import android.app.AlertDialog
import android.content.Context
import android.graphics.RectF
import android.view.View
import androidx.core.math.MathUtils
import kotlin.random.Random

class extraFun {
    private val random=Random.Default

    fun dialogBuilder(layout:Int,context: Context): AlertDialog {

        val dialogBuild = AlertDialog.Builder(context)
        val dialogView= View.inflate(context, layout, null)

        dialogBuild.setView(dialogView)
        dialogBuild.setCancelable(false)

        val dialog = dialogBuild.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        return dialog
    }
    fun isCollisionDetected(circleX: Float, circleY: Float, circleRadius: Float,rect: RectF): Boolean {
        // Find the closest point on the rectangle to the given point
        val closestX = MathUtils.clamp(circleX, rect.left, rect.right)
        val closestY = MathUtils.clamp(circleY, rect.top, rect.bottom)

        // Calculate the distance between the closest point and the given point
        val distanceX = closestX - circleX
        val distanceY = closestY - circleY
        val distance = kotlin.math.sqrt((distanceX * distanceX + distanceY * distanceY))

        return distance <= circleRadius

    }
}