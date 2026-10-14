package nl.eric.kerstlampjes

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import android.widget.RemoteViews

/** Gedeelde hulp voor de drie homescreen-widgets. */
object Widgets {
    private fun open(ctx: Context, today: Boolean, code: Int): PendingIntent {
        val i = Intent(ctx, TreeActivity::class.java)
            .putExtra("today", today)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(ctx, code, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun text(ctx: Context, title: String, sub: String, today: Boolean, code: Int): RemoteViews =
        RemoteViews(ctx.packageName, R.layout.widget_text).apply {
            setTextViewText(R.id.widget_title, title)
            setTextViewText(R.id.widget_sub, sub)
            setOnClickPendingIntent(R.id.widget_root, open(ctx, today, code))
        }

    /** Tekent de huidige boom (of haard) met je lampjes in een bitmap. */
    fun treeBitmap(ctx: Context, outW: Int = 400, outH: Int = 667): Bitmap {
        // Tekenen op schermformaat zodat de lampjes de juiste verhouding houden; daarna verkleinen.
        val w = 1080
        val h = 1800
        val big = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val r = TreeRenderer(ctx.resources.displayMetrics.density, ctx)
        r.resize(w, h)
        val p = ctx.getSharedPreferences("kerstlampjes", Context.MODE_PRIVATE)
        var st = p.getInt("tree_style", 0)
        if (TreeStyles.categoryOf(st) == 2) st = p.getInt("last_cat_0", 0)
        TreeStore.preview(ctx, r, st)
        r.draw(Canvas(big), SystemClock.uptimeMillis())
        val out = Bitmap.createScaledBitmap(big, outW, outH, true)
        big.recycle()
        return out
    }

    fun tree(ctx: Context): RemoteViews =
        RemoteViews(ctx.packageName, R.layout.widget_tree).apply {
            setImageViewBitmap(R.id.widget_image, treeBitmap(ctx))
            setOnClickPendingIntent(R.id.widget_root, open(ctx, false, 11))
        }

    /** Ververs alle widgets (bijvoorbeeld nadat je in de app iets hebt veranderd). */
    fun refreshAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        for (cls in listOf(CountdownWidget::class.java, TodayWidget::class.java, TreeWidget::class.java)) {
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, cls))
            if (ids.isNotEmpty()) {
                val i = Intent(ctx, cls).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                ctx.sendBroadcast(i)
            }
        }
    }
}

class CountdownWidget : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        Tr.init(ctx)
        val v = Widgets.text(ctx, Calendar2.countdown(), "", false, 9)
        ids.forEach { mgr.updateAppWidget(it, v) }
    }
}

class TodayWidget : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        Tr.init(ctx)
        val s = Calendar2.sceneFor()
        val title = if (s >= 0) "✨ " + Tr.t(SceneHost.names[s]) else "🎄 " + Tr.t("Kerstlampjes")
        val sub = if (s >= 0) Tr.t("Scène van vandaag") + " • " + Calendar2.countdown() else Calendar2.countdown()
        val v = Widgets.text(ctx, title, sub, s >= 0, 10)
        ids.forEach { mgr.updateAppWidget(it, v) }
    }
}

class TreeWidget : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        Tr.init(ctx)
        val v = try { Widgets.tree(ctx) } catch (e: Exception) { return }
        ids.forEach { mgr.updateAppWidget(it, v) }
    }
}
