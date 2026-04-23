package com.example.proyectosimex

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class BrumBrum : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var fontPequena: BitmapFont

    // Música
    private lateinit var musica1: Music
    private lateinit var musica2: Music
    private var canciones = arrayOfNulls<Music>(2)
    private var cancionActual = 0

    // Camión
    private var truckY = 0f
    private var velY = 0f
    private var enSuelo = true
    private val truckX = 150f
    private val truckW = 100f
    private val truckH = 50f
    private val SUELO_Y = 80f
    private val GRAVEDAD = -1800f
    private val FUERZA_SALTO = 700f

    // Obstáculos
    private val obstaculos = mutableListOf<Rectangle>()
    private var timerObst = 0f
    private val intervalo = 2f
    private val velObst = 500f

    // Progreso
    private var distancia = 0f
    private val META = 4000f

    // Vidas e invulnerabilidad
    private var vidas = 3
    private var invulnerable = 0f

    // Obstáculos superados
    private var obstaculosSaltados = 0
    private val MIN_SALTADOS = 5

    // Estado
    private enum class Estado { JUGANDO, PAUSADO, GANADO, PERDIDO }
    private var estado = Estado.JUGANDO

    // Botón pausa (esquina superior derecha)
    private lateinit var btnPausa: Rectangle
    // Botones del menú pausa
    private lateinit var btnContinuar: Rectangle
    private lateinit var btnSalir: Rectangle

    override fun create() {
        shape = ShapeRenderer()
        batch = SpriteBatch()
        font = BitmapFont().also { it.data.setScale(2.5f) }
        fontPequena = BitmapFont().also { it.data.setScale(1.8f) }

        // Cargar música
        musica1 = Gdx.audio.newMusic(Gdx.files.internal("cancion1.mp3"))
        musica2 = Gdx.audio.newMusic(Gdx.files.internal("cancion2.mp3"))
        canciones[0] = musica1
        canciones[1] = musica2

        musica1.setOnCompletionListener { siguienteCancion() }
        musica2.setOnCompletionListener { siguienteCancion() }

        canciones[0]?.play()

        reiniciar()
    }

    private fun siguienteCancion() {
        canciones[cancionActual]?.stop()
        cancionActual = (cancionActual + 1) % canciones.size
        canciones[cancionActual]?.play()
    }

    private fun reiniciar() {
        truckY = SUELO_Y; velY = 0f; enSuelo = true
        obstaculos.clear(); timerObst = 0f
        distancia = 0f; vidas = 3; invulnerable = 0f
        obstaculosSaltados = 0
        estado = Estado.JUGANDO

        // Escoger canción aleatoria al reiniciar
        canciones.forEach { it?.stop() }
        cancionActual = (Math.random() * canciones.size).toInt()
        canciones[cancionActual]?.play()
    }

    override fun render() {
        val dt = Gdx.graphics.deltaTime
        val W = Gdx.graphics.width.toFloat()
        val H = Gdx.graphics.height.toFloat()

        btnPausa = Rectangle(W - 80f, H - 70f, 60f, 50f)
        btnContinuar = Rectangle(W / 2 - 120f, H / 2, 240f, 55f)
        btnSalir = Rectangle(W / 2 - 120f, H / 2 - 80f, 240f, 55f)

        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        when (estado) {
            Estado.JUGANDO -> {
                actualizar(dt, W)
                dibujar(W, H)
                dibujarBotonPausa(W, H)
                gestionarToque(W, H)
            }
            Estado.PAUSADO -> {
                dibujar(W, H)
                dibujarMenuPausa(W, H)
                gestionarToquePausa()
            }
            Estado.GANADO, Estado.PERDIDO -> {
                dibujar(W, H)
                dibujarFinJuego(W, H)
                if (Gdx.input.justTouched()) reiniciar()
            }
        }
    }

    private fun gestionarToque(W: Float, H: Float) {
        if (!Gdx.input.justTouched()) return
        val tx = Gdx.input.x.toFloat()
        val ty = H - Gdx.input.y.toFloat()

        if (btnPausa.contains(tx, ty)) {
            estado = Estado.PAUSADO
            canciones[cancionActual]?.pause()
        } else if (enSuelo) {
            velY = FUERZA_SALTO
            enSuelo = false
        }
    }

    private fun gestionarToquePausa() {
        if (!Gdx.input.justTouched()) return
        val H = Gdx.graphics.height.toFloat()
        val tx = Gdx.input.x.toFloat()
        val ty = H - Gdx.input.y.toFloat()

        when {
            btnContinuar.contains(tx, ty) -> {
                estado = Estado.JUGANDO
                canciones[cancionActual]?.play()
            }
            btnSalir.contains(tx, ty) -> Gdx.app.exit()
        }
    }

    private fun actualizar(dt: Float, W: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && enSuelo) {
            velY = FUERZA_SALTO; enSuelo = false
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estado = Estado.PAUSADO
            return
        }

        // Física
        velY += GRAVEDAD * dt
        truckY += velY * dt
        if (truckY <= SUELO_Y) { truckY = SUELO_Y; velY = 0f; enSuelo = true }

        // Progreso — solo gana si además superó el mínimo de obstáculos
        distancia += velObst * dt
        if (distancia >= META && obstaculosSaltados >= MIN_SALTADOS) {
            estado = Estado.GANADO
            canciones[cancionActual]?.stop()
            return
        }

        // Generar obstáculos
        timerObst += dt
        if (timerObst >= intervalo) {
            timerObst = 0f
            val h = 40f + (Math.random() * 40).toFloat()
            obstaculos.add(Rectangle(W + 10f, SUELO_Y, 40f, h))
        }

        // Colisiones
        invulnerable -= dt
        val truck = Rectangle(truckX + 5, truckY, truckW - 10, truckH)
        val it = obstaculos.iterator()
        while (it.hasNext()) {
            val obs = it.next()
            obs.x -= velObst * dt
            if (obs.x + obs.width < 0) {
                it.remove()
                obstaculosSaltados++
                continue
            }
            if (invulnerable <= 0 && truck.overlaps(obs)) {
                vidas--; invulnerable = 1.5f
                if (vidas <= 0) {
                    estado = Estado.PERDIDO
                    canciones[cancionActual]?.stop()
                }
            }
        }
    }

    private fun dibujar(W: Float, H: Float) {
        shape.begin(ShapeRenderer.ShapeType.Filled)

        // Carretera
        shape.color = Color.DARK_GRAY
        shape.rect(0f, 0f, W, SUELO_Y + 5)
        shape.color = Color.YELLOW
        var x = 0f
        while (x < W) { shape.rect(x, SUELO_Y / 2 - 5, 50f, 8f); x += 80f }

        // Obstáculos
        for (obs in obstaculos) {
            shape.color = Color.BROWN
            shape.rect(obs.x, obs.y, obs.width, obs.height)
            shape.color = Color.TAN
            shape.rect(obs.x + obs.width / 2 - 3, obs.y, 6f, obs.height)
            shape.rect(obs.x, obs.y + obs.height / 2 - 3, obs.width, 6f)
        }

        // Camión
        val parpadea = invulnerable > 0 && (invulnerable * 6).toInt() % 2 == 0
        shape.color = if (parpadea) Color.GRAY else Color.RED
        shape.rect(truckX, truckY, truckW, truckH)
        shape.color = Color.FIREBRICK
        shape.rect(truckX + truckW - 30, truckY, 30f, truckH + 15)
        shape.color = Color.BLACK
        shape.circle(truckX + 20, truckY - 2, 14f)
        shape.circle(truckX + truckW - 15, truckY - 2, 14f)
        shape.color = Color.DARK_GRAY
        shape.circle(truckX + 20, truckY - 2, 8f)
        shape.circle(truckX + truckW - 15, truckY - 2, 8f)

        // Barra de progreso — se congela en 99% si faltan saltos
        val progresoEfectivo = if (obstaculosSaltados >= MIN_SALTADOS) distancia else minOf(distancia, META - 1f)
        shape.color = Color.WHITE
        shape.rect(20f, H - 40, W - 40, 18f)
        shape.color = Color.GREEN
        shape.rect(20f, H - 40, (W - 40) * (progresoEfectivo / META), 18f)

        shape.end()

        // HUD
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Vidas: $vidas", 20f, H - 50f)
        font.draw(batch, "Ruta: ${minOf((distancia / META * 100).toInt(), 99)}%", W / 2 - 80, H - 50f)
        font.draw(batch, "Saltos: $obstaculosSaltados/$MIN_SALTADOS", W - 220f, H - 50f)
        batch.end()
    }

    private fun dibujarBotonPausa(W: Float, H: Float) {
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0f, 0f, 0f, 0.5f)
        shape.rect(btnPausa.x, btnPausa.y, btnPausa.width, btnPausa.height)
        shape.color = Color.WHITE
        shape.rect(btnPausa.x + 12f, btnPausa.y + 10f, 12f, 30f)
        shape.rect(btnPausa.x + 36f, btnPausa.y + 10f, 12f, 30f)
        shape.end()
    }

    private fun dibujarMenuPausa(W: Float, H: Float) {
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0f, 0f, 0f, 0.65f)
        shape.rect(0f, 0f, W, H)

        shape.color = Color(0.15f, 0.15f, 0.15f, 1f)
        shape.rect(W / 2 - 160f, H / 2 - 120f, 320f, 220f)

        shape.color = Color(0.2f, 0.75f, 0.2f, 1f)
        shape.rect(btnContinuar.x, btnContinuar.y, btnContinuar.width, btnContinuar.height)

        shape.color = Color(0.85f, 0.2f, 0.2f, 1f)
        shape.rect(btnSalir.x, btnSalir.y, btnSalir.width, btnSalir.height)
        shape.end()

        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "PAUSA", W / 2 - 55f, H / 2 + 85f)
        fontPequena.color = Color.WHITE
        fontPequena.draw(batch, "Continuar", btnContinuar.x + 40f, btnContinuar.y + 38f)
        fontPequena.draw(batch, "Salir al menu", btnSalir.x + 20f, btnSalir.y + 38f)
        batch.end()
    }

    private fun dibujarFinJuego(W: Float, H: Float) {
        batch.begin()
        if (estado == Estado.GANADO) {
            font.color = Color.YELLOW
            font.draw(batch, "ENTREGA COMPLETADA!", W / 2 - 200, H / 2)
        } else {
            font.color = Color.RED
            font.draw(batch, "MERCANCIA DANADA!", W / 2 - 180, H / 2)
        }
        font.color = Color.WHITE
        font.draw(batch, "Toca para jugar de nuevo", W / 2 - 200, H / 2 - 55f)
        batch.end()
    }

    override fun dispose() {
        shape.dispose()
        batch.dispose()
        font.dispose()
        fontPequena.dispose()
        musica1.dispose()
        musica2.dispose()
    }
}