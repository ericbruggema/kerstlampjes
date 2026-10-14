package nl.eric.kerstlampjes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.service.quicksettings.TileService
import android.content.ComponentName
import android.view.WindowManager

/**
 * Voorgrondservice die de lampjes als overlay boven alle apps tekent.
 * De overlay is niet aanraakbaar: alle tikken gaan gewoon door naar de app eronder.
 */
class LightsService : Service() {

    companion object {
        const val ACTION_START = "nl.eric.kerstlampjes.START"
        const val ACTION_UPDATE = "nl.eric.kerstlampjes.UPDATE"
        const val ACTION_STOP = "nl.eric.kerstlampjes.STOP"
        private const val CHANNEL = "lampjes"
        private const val NOTIF_ID = 1225

        @Volatile var isRunning = false
            private set

        fun start(ctx: Context) {
            val i = Intent(ctx, LightsService::class.java).setAction(ACTION_START)
            ctx.startForegroundService(i)
        }

        fun update(ctx: Context) {
            if (isRunning) ctx.startService(Intent(ctx, LightsService::class.java).setAction(ACTION_UPDATE))
        }

        fun stop(ctx: Context) {
            if (isRunning) ctx.startService(Intent(ctx, LightsService::class.java).setAction(ACTION_STOP))
        }
    }

    private lateinit var wm: WindowManager
    private var view: LightsView? = null

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            // Animatie pauzeren als het scherm uit staat
            view?.setAnimating(i.action == Intent.ACTION_SCREEN_ON)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Tr.init(this)
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        registerReceiver(screenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        goForeground()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        val s = Prefs.load(this)
        if (view == null) addOverlay()
        view?.apply(s)
        updateWindowAlpha(s)

        isRunning = true
        refreshTile()
        return START_STICKY
    }

    private fun addOverlay() {
        val v = LightsView(this)
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= 28) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        wm.addView(v, lp)
        view = v
    }

    /**
     * Android 12+ blokkeert tikken door een overlay heen als die "te ondoorzichtig" is
     * (max 0.8). Daarom wordt de venster-alpha daar begrensd, anders is je telefoon onbedienbaar.
     */
    private fun updateWindowAlpha(s: LightSettings) {
        val v = view ?: return
        val lp = v.layoutParams as WindowManager.LayoutParams
        lp.alpha = if (Build.VERSION.SDK_INT >= 31) 0.8f else 1f
        wm.updateViewLayout(v, lp)
        v.alpha = s.opacity / 100f
    }

    private fun goForeground() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, Tr.t("Kerstlampjes"), NotificationManager.IMPORTANCE_MIN).apply {
                    description = Tr.t("Melding zolang de lampjes branden")
                    setShowBadge(false)
                }
            )
        }
        val piFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), piFlags)
        val stop = PendingIntent.getService(
            this, 1, Intent(this, LightsService::class.java).setAction(ACTION_STOP), piFlags
        )
        val n = Notification.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_bulb)
            .setContentTitle(Tr.t("Kerstlampjes branden 🎄"))
            .setContentText(Tr.t("Tik om aan te passen"))
            .setContentIntent(open)
            .setOngoing(true)
            .addAction(Notification.Action.Builder(null as android.graphics.drawable.Icon?, Tr.t("Uitzetten"), stop).build())
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun refreshTile() {
        TileService.requestListeningState(this, ComponentName(this, LightsTileService::class.java))
    }

    override fun onDestroy() {
        isRunning = false
        try { unregisterReceiver(screenReceiver) } catch (_: Exception) {}
        view?.let {
            it.setAnimating(false)
            try { wm.removeView(it) } catch (_: Exception) {}
        }
        view = null
        refreshTile()
        super.onDestroy()
    }
}
