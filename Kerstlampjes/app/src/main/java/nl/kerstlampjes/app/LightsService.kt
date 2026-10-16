package nl.kerstlampjes.app

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
        const val ACTION_START = "nl.kerstlampjes.app.START"
        const val ACTION_UPDATE = "nl.kerstlampjes.app.UPDATE"
        const val ACTION_STOP = "nl.kerstlampjes.app.STOP"
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
    private var shake: ShakeDetector? = null
    private var wantShake = false
    private var tracker: HomeOnly.Tracker? = null
    private var homeOnly = false
    private var screenOn = true
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val homeCheck = object : Runnable {
        override fun run() {
            if (!homeOnly) return
            val home = tracker?.isHome() ?: true
            val v = view
            if (v != null) {
                val vis = if (home) android.view.View.VISIBLE else android.view.View.GONE
                if (v.visibility != vis) v.visibility = vis
                v.setAnimating(home && screenOn)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            // Animatie pauzeren als het scherm uit staat
            val on = i.action == Intent.ACTION_SCREEN_ON
            screenOn = on
            view?.setAnimating(on && (!homeOnly || view?.visibility == android.view.View.VISIBLE))
            if (on && wantShake) shake?.start() else shake?.stop()
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
            getSharedPreferences("kerstlampjes", MODE_PRIVATE).edit().putBoolean("lights_on", false).apply()
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
        // Schudden haalt de sneeuw weg; de sensor is alleen aan als er sneeuw kan blijven liggen
        handler.removeCallbacks(homeCheck)
        homeOnly = s.homeOnly
        if (homeOnly) {
            if (tracker == null) tracker = HomeOnly.Tracker(this)
            handler.post(homeCheck)
        } else {
            view?.visibility = android.view.View.VISIBLE
            view?.setAnimating(screenOn)
        }
        wantShake = (s.decor and Decor.PILE) != 0
        if (wantShake) {
            if (shake == null) shake = ShakeDetector(this) { view?.post { view?.shake() } }
            shake?.start()
        } else {
            shake?.stop()
        }
        AudioLevel.sync(this, s.mode == TreeModes.MUSIC, "overlay")

        isRunning = true
        // Onthouden dat de lampjes aan staan, zodat ze na een herstart van de telefoon terugkomen
        getSharedPreferences("kerstlampjes", MODE_PRIVATE).edit().putBoolean("lights_on", true).apply()
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
            // De microfoon in een voorgrondservice vraagt om het type "microphone" (alleen als dat nodig is en mag).
            val wantMic = Prefs.load(this).mode == TreeModes.MUSIC && AudioLevel.hasPermission(this)
            try {
                val type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE or
                    (if (wantMic) ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE else 0)
                startForeground(NOTIF_ID, n, type)
            } catch (e: Exception) {
                startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            }
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun refreshTile() {
        TileService.requestListeningState(this, ComponentName(this, LightsTileService::class.java))
    }

    override fun onDestroy() {
        isRunning = false
        AudioLevel.sync(this, false, "overlay")
        shake?.stop()
        handler.removeCallbacks(homeCheck)
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
