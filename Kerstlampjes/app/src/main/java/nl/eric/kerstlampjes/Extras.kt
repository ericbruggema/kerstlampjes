package nl.eric.kerstlampjes

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Eigen (instelbare) voorkeuren van de extra's. */
object ExtraPrefs {
    private fun p(ctx: Context) = ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE)
    fun sound(ctx: Context) = p(ctx).getBoolean("x_sound", false)
    fun setSound(ctx: Context, v: Boolean) = p(ctx).edit().putBoolean("x_sound", v).apply()
    fun dailyAuto(ctx: Context) = p(ctx).getBoolean("x_daily_auto", true)
    fun setDailyAuto(ctx: Context, v: Boolean) = p(ctx).edit().putBoolean("x_daily_auto", v).apply()
    fun notify(ctx: Context) = p(ctx).getBoolean("x_notify", false)
    fun setNotify(ctx: Context, v: Boolean) = p(ctx).edit().putBoolean("x_notify", v).apply()
    fun song(ctx: Context) = p(ctx).getInt("x_song", 0)
    fun setSong(ctx: Context, v: Int) = p(ctx).edit().putInt("x_song", v).apply()
    /** 0 = normaal (30 fps), 1 = zuinig (15 fps), 2 = automatisch */
    fun fps(ctx: Context) = p(ctx).getInt("x_fps", 2)
    fun setFps(ctx: Context, v: Int) = p(ctx).edit().putInt("x_fps", v).apply()
    fun lastDay(ctx: Context) = p(ctx).getString("x_last_day", "") ?: ""
    fun setLastDay(ctx: Context, v: String) = p(ctx).edit().putString("x_last_day", v).apply()
}

/** Kerst-agenda: aftellen en de scène van de dag. */
object Calendar2 {
    /** Seizoen: 15 november t/m 6 januari. */
    fun inSeason(d: LocalDate): Boolean {
        val m = d.monthValue
        return (m == 11 && d.dayOfMonth >= 15) || m == 12 || (m == 1 && d.dayOfMonth <= 6)
    }

    fun countdown(d: LocalDate = LocalDate.now()): String {
        val y = d.year
        val xmas = LocalDate.of(y, 12, 25)
        val ny = LocalDate.of(y, 12, 31)
        val kings = LocalDate.of(y, 1, 6)
        fun days(to: LocalDate) = ChronoUnit.DAYS.between(d, to).toInt()
        return when {
            d == xmas -> Tr.t("🎄 Het is kerst! Vrolijk kerstfeest")
            d.isAfter(xmas) && !d.isAfter(ny) -> when (days(ny)) {
                0 -> Tr.t("🎆 Het is oudejaarsavond!")
                1 -> Tr.t("🎆 Nog 1 dag tot oudejaarsavond")
                else -> Tr.f("🎆 Nog %d dagen tot oudejaarsavond", days(ny))
            }
            d.monthValue == 1 && d.dayOfMonth == 1 -> Tr.t("🥳 Gelukkig nieuwjaar!")
            !d.isAfter(kings) && d.monthValue == 1 -> when (days(kings)) {
                0 -> Tr.t("⭐ Het is Driekoningen")
                1 -> Tr.t("⭐ Nog 1 dag tot Driekoningen")
                else -> Tr.f("⭐ Nog %d dagen tot Driekoningen", days(kings))
            }
            else -> {
                val n = days(xmas)
                if (n == 1) Tr.t("🎄 Nog 1 dag tot kerst — morgen is het zover!") else Tr.f("🎄 Nog %d dagen tot kerst", n)
            }
        }
    }

    /** Zondagen van de advent (4 zondagen voor 25 december). */
    private fun isAdventSunday(d: LocalDate): Boolean {
        if (d.dayOfWeek != DayOfWeek.SUNDAY) return false
        val s4 = LocalDate.of(d.year, 12, 24).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return d == s4 || d == s4.minusWeeks(1) || d == s4.minusWeeks(2) || d == s4.minusWeeks(3)
    }

    /** Index in SceneHost (0..8) die bij vandaag past, of -1 buiten het seizoen. */
    fun sceneFor(d: LocalDate = LocalDate.now()): Int {
        if (!inSeason(d)) return -1
        val m = d.monthValue
        val day = d.dayOfMonth
        return when {
            m == 1 -> if (day == 1) 0 else 6          // sneeuwbol / ijsbaan
            m == 12 && day == 24 -> 7                 // kerststal
            m == 12 && day == 5 -> 8                  // pakjesavond: cadeautjes
            isAdventSunday(d) -> 2                    // adventskrans (ook eind november)
            m == 11 -> 3                              // kerstdorp
            day <= 23 -> 1                            // adventskalender
            day == 25 || day == 26 -> 8               // cadeautjes
            day == 31 -> 0                            // sneeuwbol
            else -> 4                                 // slee
        }
    }

    fun todayStyle(): Int {
        val s = sceneFor()
        return if (s < 0) -1 else TreeStyles.TREES + HearthStyles.all.size + s
    }
}

/** Eigen foto als achtergrond (bestand in de app-map, ook voor de wallpaper). */
object OwnPhoto {
    fun file(ctx: Context) = File(ctx.filesDir, "eigen-foto.jpg")
    fun exists(ctx: Context) = file(ctx).exists()

    fun load(ctx: Context): Bitmap? = try {
        BitmapFactory.decodeFile(file(ctx).path)
    } catch (e: Exception) {
        null
    }

    /** Leest een gekozen afbeelding, verkleint die en bewaart hem. */
    fun import(ctx: Context, uri: android.net.Uri): Boolean = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outHeight / sample > 2600 || bounds.outWidth / sample > 1800) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bmp = ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (bmp == null) false else {
            file(ctx).outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 88, it) }
            bmp.recycle()
            true
        }
    } catch (e: Exception) {
        false
    }
}

/** Geluid, volledig zelf gesynthetiseerd: knetterend vuur, kerstliedjes (speeldoos) en een belletje. */
object Sound {
    private const val RATE = 22050
    const val OFF = 0
    const val FIRE = 1
    const val MUSIC = 2

    @Volatile private var mode = OFF
    private var running = false
    @Volatile var enabled = false
    /** 0..songNames.size-2 = dat liedje, laatste = alle liedjes na elkaar. */
    @Volatile var song = 0

    val songNames = arrayOf("Jingle Bells", "Stille nacht", "We wish you a merry Christmas", "Deck the halls", "Alle liedjes")

    fun set(newMode: Int) {
        synchronized(this) {
            mode = if (enabled) newMode else OFF
            if (mode != OFF && !running) {
                running = true
                Thread(::loop, "kl-sound").also { it.isDaemon = true; it.start() }
            }
        }
    }

    private fun track(bytes: Int, stream: Boolean): AudioTrack = AudioTrack.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        .setAudioFormat(
            AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(RATE).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()
        )
        .setBufferSizeInBytes(bytes)
        .setTransferMode(if (stream) AudioTrack.MODE_STREAM else AudioTrack.MODE_STATIC)
        .build()

    private val semis = mapOf('C' to 0, 'D' to 2, 'E' to 4, 'F' to 5, 'G' to 7, 'A' to 9, 'B' to 11)

    /** "F#4" -> frequentie in Hz. */
    private fun freq(n: String): Double {
        var s = semis[n[0]]!!
        var i = 1
        if (n[i] == '#') { s++; i++ } else if (n[i] == 'b') { s--; i++ }
        val midi = 12 * (n.substring(i).toInt() + 1) + s
        return 440.0 * Math.pow(2.0, (midi - 69) / 12.0)
    }

    /** "E4:2 G4:3 R:2" -> (frequentie, lengte in achtsten); R = rust. */
    private fun parse(s: String): Array<DoubleArray> =
        s.trim().split(Regex("\\s+")).map { tok ->
            val p = tok.split(':')
            doubleArrayOf(if (p[0] == "R") 0.0 else freq(p[0]), p[1].toDouble())
        }.toTypedArray()

    private val songs: Array<Array<DoubleArray>> by lazy {
        arrayOf(
            parse("E4:2 E4:2 E4:4 E4:2 E4:2 E4:4 E4:2 G4:2 C4:3 D4:1 E4:8 F4:2 F4:2 F4:3 F4:1 F4:2 E4:2 E4:2 E4:1 E4:1 E4:2 D4:2 D4:2 E4:2 D4:4 G4:4 R:6"),
            parse("G4:3 A4:1 G4:2 E4:6 G4:3 A4:1 G4:2 E4:6 D5:4 D5:2 B4:6 C5:4 C5:2 G4:6 A4:4 A4:2 C5:3 B4:1 A4:2 G4:3 A4:1 G4:2 E4:6 A4:4 A4:2 C5:3 B4:1 A4:2 G4:3 A4:1 G4:2 E4:6 D5:4 D5:2 F5:3 D5:1 B4:2 C5:6 E5:6 C5:3 G4:2 E4:3 G4:2 F4:3 D4:1 C4:6 R:6"),
            parse("D4:2 G4:2 G4:1 A4:1 G4:1 F#4:1 E4:2 E4:2 E4:2 A4:2 A4:1 B4:1 A4:1 G4:1 F#4:2 D4:2 D4:2 B4:2 B4:1 C5:1 B4:1 A4:1 G4:2 E4:2 D4:1 D4:1 E4:2 A4:2 F#4:2 G4:6 R:4"),
            parse("G4:3 F4:1 E4:2 D4:2 C4:2 D4:2 E4:2 C4:2 D4:1 E4:1 F4:1 D4:1 E4:3 D4:1 C4:2 B3:2 C4:4 R:4")
        )
    }

    /** Seconden per achtste noot per liedje. */
    private val tempo = doubleArrayOf(0.21, 0.30, 0.22, 0.20)

    private fun loop() {
        val size = max(AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), 8192)
        val t = try { track(size, true) } catch (e: Exception) { synchronized(this) { running = false }; return }
        val buf = ShortArray(1024)
        val rnd = Random(System.nanoTime())
        var brown = 0f
        var pop = 0f
        var popDecay = 0.95f
        var note = 0
        var inNote = 0
        var current = 0
        var selected = -1
        val echoLen = (RATE * 0.23).toInt()
        val echo = FloatArray(echoLen)
        var ep = 0
        try {
            t.play()
            while (true) {
                val m = synchronized(this) {
                    if (mode == OFF) { running = false; -1 } else mode
                }
                if (m < 0) break
                val sel = song.coerceIn(0, songs.size)
                if (sel != selected) {
                    selected = sel
                    current = if (sel >= songs.size) 0 else sel
                    note = 0; inNote = 0
                }
                var env = 0f
                for (i in buf.indices) {
                    var v = 0f
                    if (m == FIRE) {
                        brown = brown * 0.985f + (rnd.nextFloat() - 0.5f) * 0.05f
                        if (rnd.nextFloat() < 0.0007f) { pop = 0.25f + rnd.nextFloat() * 0.5f; popDecay = 0.90f + rnd.nextFloat() * 0.07f }
                        v = brown * 0.9f + (rnd.nextFloat() - 0.5f) * pop
                        pop *= popDecay
                        v *= 0.8f
                    } else {
                        val melody = songs[current]
                        val n = melody[note]
                        val len = (n[1] * tempo[current] * RATE).toInt()
                        if (n[0] > 0.0) {
                            val tt = inNote / RATE.toDouble()
                            val e = exp(-tt * (if (n[1] >= 3) 2.2 else 3.4)).toFloat()
                            env = e
                            val f = n[0]
                            var s = sin(2 * PI * f * tt) + 0.35 * sin(2 * PI * f * 2 * tt) + 0.12 * sin(2 * PI * f * 3 * tt)
                            if (n[1] >= 3) s += 0.5 * sin(2 * PI * f * 0.5 * tt)
                            v = (s * e * 0.15).toFloat()
                        }
                        if (++inNote >= len) {
                            inNote = 0
                            note++
                            if (note >= melody.size) {
                                note = 0
                                if (sel >= songs.size) current = (current + 1) % songs.size
                            }
                        }
                        val delayed = echo[ep]
                        echo[ep] = v + delayed * 0.32f
                        ep = (ep + 1) % echoLen
                        v += delayed * 0.35f
                    }
                    buf[i] = (v.coerceIn(-1f, 1f) * 32000f * 0.5f).toInt().toShort()
                }
                AudioLevel.synth = if (m == MUSIC) min(1f, env * 0.9f) else 0f
                t.write(buf, 0, buf.size)
            }
        } catch (e: Exception) {
            synchronized(this) { running = false }
        } finally {
            AudioLevel.synth = 0f
            try { t.stop() } catch (e: Exception) {}
            t.release()
        }
    }

    private val pentatonic = doubleArrayOf(523.25, 587.33, 659.25, 783.99, 880.0, 1046.5)

    /** Eén helder belletje (bij een tik). */
    fun ding() {
        if (!enabled) return
        Thread {
            try {
                val f = pentatonic[Random.nextInt(pentatonic.size)]
                val n = (RATE * 0.7).toInt()
                val data = ShortArray(n) { i ->
                    val tt = i / RATE.toDouble()
                    val s = sin(2 * PI * f * tt) + 0.3 * sin(2 * PI * f * 2.76 * tt)
                    (s * exp(-tt * 6.0) * 0.35 * 32000 * 0.5).toInt().toShort()
                }
                val t = track(n * 2, false)
                t.write(data, 0, n)
                t.play()
                Thread.sleep(900)
                t.release()
            } catch (e: Exception) {
            }
        }.also { it.isDaemon = true }.start()
    }
}

/** Geluidsniveau van de microfoon (0..1), voor "Op de muziek". */
object AudioLevel {
    @Volatile var mic = 0f
    @Volatile var synth = 0f
    /** Het hoogste van de microfoon en de eigen muziek. */
    val level: Float get() = if (mic > synth) mic else synth
    @Volatile private var want = false
    private var running = false

    fun hasPermission(ctx: Context) =
        ctx.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    fun sync(ctx: Context, wanted: Boolean) {
        want = wanted && hasPermission(ctx)
        if (!want) { mic = 0f; return }
        synchronized(this) {
            if (running) return
            running = true
        }
        Thread({ record() }, "kl-mic").also { it.isDaemon = true; it.start() }
    }

    private fun record() {
        val rate = 16000
        val min = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        var rec: AudioRecord? = null
        try {
            rec = AudioRecord(MediaRecorder.AudioSource.MIC, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, max(min, 4096))
            if (rec.state != AudioRecord.STATE_INITIALIZED) return
            rec.startRecording()
            val b = ShortArray(512)
            while (want) {
                val n = rec.read(b, 0, b.size)
                if (n <= 0) continue
                var sum = 0.0
                for (i in 0 until n) sum += b[i] * b[i].toDouble()
                val rms = sqrt(sum / n).toFloat()
                val target = min(1f, rms / 5000f)
                mic = if (target > mic) target else mic * 0.88f
            }
        } catch (e: Exception) {
        } finally {
            try { rec?.stop() } catch (e: Exception) {}
            rec?.release()
            mic = 0f
            synchronized(this) { running = false }
        }
    }
}

/** Dagelijkse melding (18:00) tijdens het kerstseizoen. */
class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Tr.init(ctx)
        if (!ExtraPrefs.notify(ctx)) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            schedule(ctx)
            return
        }
        val today = LocalDate.now()
        if (!Calendar2.inSeason(today)) return
        val scene = Calendar2.sceneFor(today)
        val name = if (scene >= 0) Tr.t(SceneHost.names[scene]) else ""
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel("daily", Tr.t("Dagelijkse kerstmelding"), NotificationManager.IMPORTANCE_DEFAULT))
        val open = Intent(ctx, TreeActivity::class.java).putExtra("today", true)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pi = PendingIntent.getActivity(ctx, 7, open, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n = android.app.Notification.Builder(ctx, "daily")
            .setSmallIcon(R.drawable.ic_bulb)
            .setContentTitle(Calendar2.countdown(today))
            .setContentText(if (name.isEmpty()) Tr.t("Open Kerstlampjes") else Tr.f("Vandaag: %s — tik om te openen", name))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        if (ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            nm.notify(7, n)
        }
    }

    companion object {
        private fun pending(ctx: Context) = PendingIntent.getBroadcast(
            ctx, 8, Intent(ctx, DailyReceiver::class.java).setAction("nl.eric.kerstlampjes.DAILY"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        fun schedule(ctx: Context) {
            val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val c = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 18); set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pending(ctx))
        }

        fun cancel(ctx: Context) {
            (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending(ctx))
        }
    }
}
