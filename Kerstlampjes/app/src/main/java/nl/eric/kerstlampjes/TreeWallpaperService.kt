package nl.eric.kerstlampjes

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

/** Live wallpaper met de zelfgebouwde kerstboom. Tik op een lampje om de kleur te veranderen. */
class TreeWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = TreeEngine()

    private inner class TreeEngine : Engine() {
        private val renderer = TreeRenderer(resources.displayMetrics.density, this@TreeWallpaperService)
        private val scenes = SceneHost(this@TreeWallpaperService)
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private var sized = false

        private val tick = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) handler.postDelayed(this, Perf.frameDelay(this@TreeWallpaperService))
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Tr.init(this@TreeWallpaperService)
            setTouchEventsEnabled(true)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            renderer.resize(width, height)
            scenes.resize(width.toFloat(), height.toFloat())
            TreeStore.load(this@TreeWallpaperService, renderer)
            sized = true
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (!visible) AudioLevel.sync(this@TreeWallpaperService, false)
            handler.removeCallbacks(tick)
            if (visible) {
                // Pak wijzigingen uit de app op
                if (sized) TreeStore.load(this@TreeWallpaperService, renderer)
                renderer.reloadOwn()
                AudioLevel.sync(this@TreeWallpaperService, renderer.mode == TreeModes.MUSIC)
                handler.post(tick)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            handler.removeCallbacks(tick)
            super.onSurfaceDestroyed(holder)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (event.actionMasked == MotionEvent.ACTION_UP && sized && renderer.isScene) {
                scenes.scene(renderer.sceneIndex).tap(event.x, event.y, SystemClock.uptimeMillis())
                return
            }
            if (event.actionMasked == MotionEvent.ACTION_UP && sized) {
                if (renderer.isFireplace && renderer.hitFire(event.x, event.y)) {
                    renderer.flare(SystemClock.uptimeMillis())
                    return
                }
                val i = renderer.hit(event.x, event.y)
                if (i >= 0) {
                    renderer.tap(event.x, event.y, 0, SystemClock.uptimeMillis())
                    TreeStore.save(this@TreeWallpaperService, renderer)
                }
            }
        }

        private fun drawFrame() {
            if (!sized) return
            val holder = surfaceHolder
            val c = try { holder.lockHardwareCanvas() } catch (e: Exception) { null } ?: return
            try {
                if (renderer.isScene) scenes.scene(renderer.sceneIndex).draw(c, SystemClock.uptimeMillis())
                else renderer.draw(c, SystemClock.uptimeMillis())
            } finally {
                holder.unlockCanvasAndPost(c)
            }
        }
    }
}
