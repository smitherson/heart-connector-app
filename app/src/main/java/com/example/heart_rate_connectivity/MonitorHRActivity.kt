package com.example.heart_rate_connectivity

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.androidplot.Plot
import com.androidplot.util.PixelUtils
import com.androidplot.util.Redrawer
import com.androidplot.xy.*
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.*
import kotlin.collections.ArrayList


class MonitorHRActivity : AppCompatActivity() {

    var hrDevicesGatt: ArrayList<HeartRateGatt?> = arrayListOf<HeartRateGatt?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monitor_hr_activity)

        val manageDevicesButton = findViewById(R.id.manageDevicesButton) as Button
        val reset_tracking = findViewById(R.id.reset_tracking) as Button
        val connect_button = findViewById(R.id.connect) as Button


        hrDevicesGatt.clear()

        connect_button.setOnClickListener {

            val app = this.application as ApplicationHR

            val selectedDevicesList = arrayListOf<String>("E6:18:78:10:14:7B", "CD:95:52:7E:AA:4F")
            hrDevicesGatt.clear()
            for (mac in selectedDevicesList /*app.bluetoothManager.selectedDevicesList*/) {
                hrDevicesGatt.add(
                    HeartRateGatt(
                        mac,
                        applicationContext,
                        app.bluetoothManager.getAdapter()
                    )
                )
            }
        }

        manageDevicesButton.setOnClickListener {
            val intent = Intent(this, DevicesManageActivity::class.java)
            startActivity(intent)
        }


        reset_tracking.setOnClickListener {
           // doRedraw()
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                override fun run() {
                    doRedraw()
                    mainHandler.postDelayed(this, 500)
                }
            })
        }



        //doRedraw()
        //val heartRatePlot = findViewById(R.id.heartRatePlot) as XYPlot
        //heartRatePlot.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);
        //heartRatePlot.renderMode = Plot.RenderMode.USE_BACKGROUND_THREAD

    }

    fun doRedraw() {


        val heartRatePlot = findViewById(R.id.heartRatePlot) as XYPlot
        val RRatePlot = findViewById(R.id.RRatePlot) as XYPlot

        heartRatePlot.clear()
        RRatePlot.clear()

        heartRatePlot.setRangeBoundaries(25, BoundaryMode.FIXED, 190, BoundaryMode.FIXED)
        RRatePlot.setRangeBoundaries(450, BoundaryMode.FIXED, 1100, BoundaryMode.FIXED)

        //val domainLabels =
        //    hrDevicesGatt[0]?.domainLabels ?: arrayListOf<Number>(1, 2, 3, 6, 7, 8, 9, 10, 13, 14)

        var i = 0
        for (hrGatt in hrDevicesGatt) {

            var series1Numbers =
                hrGatt?.hrMeasurments ?: arrayListOf<Number>(1, 4, 2, 8, 4, 16, 8, 32, 16, 64)
            var series2Numbers = hrGatt?.rrMeasurments ?: arrayListOf<Number>(
                5,
                2,
                10,
                5,
                20,
                10,
                40,
                20,
                80,
                40
            )


            val series1: XYSeries = SimpleXYSeries(
                series1Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, hrGatt?.name
            )
            val series2: XYSeries = SimpleXYSeries(
                series2Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, hrGatt?.name
            )

            val series1Format =
                LineAndPointFormatter(Color.RED, Color.RED, Color.TRANSPARENT, null)
            val series2Format =
                LineAndPointFormatter(Color.GREEN, Color.GREEN, Color.TRANSPARENT, null)
            i++
            if (i % 2 == 0) {
                heartRatePlot.addSeries(series1, series1Format)
                RRatePlot.addSeries(series2, series1Format)
            } else {
                heartRatePlot.addSeries(series1,series2Format )
                RRatePlot.addSeries(series2, series2Format)
            }
        }
        /*heartRatePlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
            .setFormat(object : Format() {
                override fun format(
                    obj: Any,
                    toAppendTo: StringBuffer,
                    pos: FieldPosition?
                ): StringBuffer? {
                    val i = Math.round((obj as Number).toFloat())
                    return toAppendTo.append(domainLabels[i])
                }

                override fun parseObject(source: String?, pos: ParsePosition?): Any? {
                    return null
                }
            })
         */
        heartRatePlot.redraw()
        RRatePlot.redraw()

    }

}