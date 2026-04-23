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

// ─────────────────────────────────────────────
//  MEJORAS ROGUELIKE
// ─────────────────────────────────────────────

enum class Rareza { COMUN, RARO, EPICO, LEGENDARIO }

data class Mejora(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val emoji: String,
    val rareza: Rareza
                 )

val TODAS_LAS_MEJORAS = listOf(
    // COMUNES
    Mejora("vida",          "+1 Vida",              "Un repuesto de emergencia. Por si acaso.",          "[VID]",   Rareza.COMUN),
    Mejora("salto",         "Turbo de Salto",        "Mas fuerza en los muelles. +100 potencia.",         "[SLT]",   Rareza.COMUN),
    Mejora("gravedad",      "Gravedad Reducida",     "El camion pesa menos hoy. Raro pero util.",         "[GRV]",   Rareza.COMUN),
    Mejora("hitbox",        "Camion Compacto",       "Lo retiran un poco. Hitbox mas pequena.",           "[HIT]",   Rareza.COMUN),
    Mejora("invul",         "Chasis Reforzado",      "Invulnerabilidad dura mas tras un golpe.",          "[INV]",   Rareza.COMUN),
    Mejora("velObst",       "Atascos en Ruta",       "Los obstaculos van mas despacio. Suerte.",          "[LNT]",   Rareza.COMUN),

    // RAROS
    Mejora("escudo",        "Escudo Bumper",         "Absorbe 1 golpe gratis. Luego explota.",            "[ESC]",   Rareza.RARO),
    Mejora("dobleJump",     "Doble Eje",             "Puedes saltar una vez mas en el aire.",             "[DBL]",   Rareza.RARO),
    Mejora("progExtra",     "Atajos GPS",            "La distancia avanza un 20% mas rapido.",            "[GPS]",   Rareza.RARO),
    Mejora("romperPrimero", "Parachoques Duro",      "Al chocar, destruyes el obstaculo sin perder vida.","[DRO]",   Rareza.RARO),
    Mejora("tiempoBala",    "Tiempo Bala",           "Al saltar, el mundo va a camara lenta 0.5s.",       "[BAL]",   Rareza.RARO),

    // EPICOS
    Mejora("bolaFuego",     "Bola de Fuego",         "Destruye el primer obstaculo. 1 uso por ruta.",     "[FIRE]",  Rareza.EPICO),
    Mejora("fantasma",      "Modo Fantasma",         "2 segundos de colisiones desactivadas.",            "[FTM]",   Rareza.EPICO),
    Mejora("obstPequeños",  "Carga Ligera",          "Los obstaculos aparecen un 30% mas pequenos.",      "[MNI]",   Rareza.EPICO),
    Mejora("segundaOport",  "Segunda Oportunidad",   "Si mueres, vuelves con 1 vida. Una vez.",           "[2ND]",   Rareza.EPICO),

    // LEGENDARIOS
    Mejora("explosion",     "Explosion de Ira",      "Al recibir dano, destruyes todos los obstaculos.",  "[EXP]",   Rareza.LEGENDARIO),
    Mejora("slowPerm",      "Modo Tortuga",          "TODO va mas lento de forma permanente.",            "[TRT]",   Rareza.LEGENDARIO),
    Mejora("tripleRec",     "Azar Triple",           "En la proxima eleccion aparecen 5 cartas.",         "[AZR]",   Rareza.LEGENDARIO)
                              )

// ─────────────────────────────────────────────
//  JUEGO PRINCIPAL
// ─────────────────────────────────────────────

class BrumBrum : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var fontPequena: BitmapFont
    private lateinit var fontTitulo: BitmapFont

    // Música
    private lateinit var musica1: Music
    private lateinit var musica2: Music
    private var canciones = arrayOfNulls<Music>(2)
    private var cancionActual = 0

    // Camión — valores BASE (se restauran entre rutas si hace falta)
    private var truckY = 0f
    private var velY = 0f
    private var enSuelo = true
    private val truckX = 150f
    private var truckW = 100f
    private var truckH = 50f
    private val SUELO_Y = 80f

    // ─── Contador de stacks por mejora ───────────────────────────────────────
    // Cada entrada guarda cuántas veces se ha cogido esa mejora
    private val stacksMejoras = mutableMapOf<String, Int>()

    private fun stacksDe(id: String) = stacksMejoras.getOrDefault(id, 0)
    private fun tieneUpgrade(id: String) = stacksDe(id) > 0

    // Stats modificables por mejoras (se recalculan desde cero en cada reiniciarRuta)
    private var gravedad = -1800f
    private var fuerzaSalto = 700f
    private var velObst = 500f
    private var duracionInvul = 1.5f
    private var multiplicadorProgreso = 1f
    private var cartasExtra = 0

    // Obstáculos
    private val obstaculos = mutableListOf<Rectangle>()
    private var timerObst = 0f
    private val intervalo = 2f
    private var alturaMultObst = 1f

    // Progreso
    private var distancia = 0f
    private var obstaculosSaltados = 0
    private var minSaltadosDinamico = 5

    // Vidas e invulnerabilidad
    private var vidas = 3
    private var invulnerable = 0f

    // ─── Power-ups activos en esta ruta ──────────────────────────────────────
    private var escudosRestantes = 0         // stackea: 1 escudo por stack de "escudo"
    private var saltosMaximos = 1            // 1 base + 1 por cada stack de dobleJump
    private var saltosRestantes = 1
    private var bolasRestantes = 0           // 1 bola por stack de bolaFuego
    private var tiempoBalaActivo = 0f
    private var fantasmaActivo = 0f
    private var rompeRestantes = 0           // 1 rotura gratis por stack de romperPrimero
    private var segundasOportRestantes = 0   // 1 por stack de segundaOport
    private var explosionesRestantes = 0     // 1 por stack de explosion
    private var slowPermActivo = false

    // ─── Estados ───
    private enum class Estado { JUGANDO, PAUSADO, ELIGIENDO, GANADO, PERDIDO }
    private var estado = Estado.JUGANDO

    // Botones HUD
    private lateinit var btnPausa: Rectangle
    private lateinit var btnContinuar: Rectangle
    private lateinit var btnSalir: Rectangle
    private lateinit var btnBolaFuego: Rectangle

    // ─── Elección de mejoras ───
    private var opcionesMejora = mutableListOf<Mejora>()
    private val rectOpciones = Array(5) { Rectangle() }
    private var cantOpciones = 3
    private var rutasCompletadas = 0

    // Proyectil bola de fuego
    private var bolaX = -999f
    private var bolaY = -999f
    private var bolaActiva = false

    override fun create() {
        shape = ShapeRenderer()
        batch = SpriteBatch()
        font = BitmapFont().also { it.data.setScale(2.5f) }
        fontPequena = BitmapFont().also { it.data.setScale(1.8f) }
        fontTitulo = BitmapFont().also { it.data.setScale(3.5f) }

        musica1 = Gdx.audio.newMusic(Gdx.files.internal("cancion1.mp3"))
        musica2 = Gdx.audio.newMusic(Gdx.files.internal("cancion2.mp3"))
        canciones[0] = musica1
        canciones[1] = musica2
        musica1.setOnCompletionListener { siguienteCancion() }
        musica2.setOnCompletionListener { siguienteCancion() }
        canciones[0]?.play()

        reiniciarTotal()
    }

    // ─── Reinicio TOTAL (nueva partida) ──────────────────────────────────────
    private fun reiniciarTotal() {
        stacksMejoras.clear()
        rutasCompletadas = 0
        slowPermActivo = false
        truckW = 100f
        truckH = 50f
        reiniciarRuta()
        estado = Estado.JUGANDO
        cancionActual = (Math.random() * canciones.size).toInt()
        canciones[cancionActual]?.play()
    }

    // ─── Recalcula todos los stats a partir de los stacks acumulados ──────────
    // Se llama cada vez que empieza una ruta nueva para aplicar bien todo.
    private fun recalcularStats() {
        // Resetear a base
        gravedad = -1800f
        fuerzaSalto = 700f
        velObst = 500f
        duracionInvul = 1.5f
        multiplicadorProgreso = 1f
        alturaMultObst = 1f
        cartasExtra = 0
        truckW = 100f
        truckH = 50f

        // Aplicar cada mejora tantas veces como stacks tenga
        repeat(stacksDe("salto"))      { fuerzaSalto += 100f }
        repeat(stacksDe("gravedad"))   { gravedad *= 0.9f }
        repeat(stacksDe("hitbox"))     { truckW *= 0.85f; truckH *= 0.85f }
        repeat(stacksDe("invul"))      { duracionInvul += 1f }
        repeat(stacksDe("progExtra"))  { multiplicadorProgreso += 0.2f }
        repeat(stacksDe("obstPequeños")) { alturaMultObst *= 0.7f }
        // tripleRec: solo importa la cantidad de cartas en la próxima elección
        if (stacksDe("tripleRec") > 0) cartasExtra = stacksDe("tripleRec") * 2

        // velObst se aplica después de la dificultad progresiva, ver aplicarDificultadProgresiva()

        // Slow perm: se activa si tienes al menos 1 stack
        if (stacksDe("slowPerm") > 0) {
            slowPermActivo = true
        }
    }

    // ─── Reinicio de RUTA ────────────────────────────────────────────────────
    private fun reiniciarRuta() {
        recalcularStats()
        aplicarDificultadProgresiva()   // ajusta velObst según ruta + stacks de velObst/slowPerm

        truckY = SUELO_Y; velY = 0f; enSuelo = true
        obstaculos.clear(); timerObst = 0f
        distancia = 0f; vidas = 3 + stacksDe("vida")   // vida base + stacks
        invulnerable = 0f
        obstaculosSaltados = 0

        // Power-ups que se recargan cada ruta según stacks
        escudosRestantes    = stacksDe("escudo")
        saltosMaximos       = 1 + stacksDe("dobleJump")   // 1 base + 1 por stack
        saltosRestantes     = saltosMaximos
        bolasRestantes      = stacksDe("bolaFuego")
        rompeRestantes      = stacksDe("romperPrimero")
        segundasOportRestantes = stacksDe("segundaOport")
        explosionesRestantes   = stacksDe("explosion")

        bolaActiva = false; bolaX = -999f
        tiempoBalaActivo = 0f
        fantasmaActivo = 0f

        estado = Estado.JUGANDO
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SORTEO DE MEJORAS
    // ─────────────────────────────────────────────────────────────────────────
    private fun sortearMejoras() {
        // Con tripleRec: 3 cartas base + 2 extra por cada stack
        cantOpciones = 3 + (stacksDe("tripleRec") * 2).coerceAtMost(2)  // máx 5
        opcionesMejora.clear()
        val disponibles = TODAS_LAS_MEJORAS.toMutableList()
        repeat(cantOpciones) {
            if (disponibles.isEmpty()) return@repeat
            val rareza = sortearRareza()
            val pool = disponibles.filter { it.rareza == rareza }
            val elegida = if (pool.isNotEmpty()) pool.random() else disponibles.random()
            opcionesMejora.add(elegida)
            disponibles.remove(elegida)
        }
    }

    private fun sortearRareza(): Rareza {
        val r = Math.random() * 100
        return when {
            r < 50  -> Rareza.COMUN
            r < 80  -> Rareza.RARO
            r < 95  -> Rareza.EPICO
            else    -> Rareza.LEGENDARIO
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  APLICAR MEJORA ELEGIDA — solo incrementa el stack, reiniciarRuta recalcula
    // ─────────────────────────────────────────────────────────────────────────
    private fun aplicarMejora(m: Mejora) {
        stacksMejoras[m.id] = stacksDe(m.id) + 1
        rutasCompletadas++
        reiniciarRuta()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  RENDER LOOP
    // ─────────────────────────────────────────────────────────────────────────
    override fun render() {
        val dt = Gdx.graphics.deltaTime
        val W = Gdx.graphics.width.toFloat()
        val H = Gdx.graphics.height.toFloat()

        btnPausa      = Rectangle(W - 80f,  H - 70f,   60f,  50f)
        btnContinuar  = Rectangle(W / 2 - 120f, H / 2,  240f, 55f)
        btnSalir      = Rectangle(W / 2 - 120f, H / 2 - 80f, 240f, 55f)
        btnBolaFuego  = Rectangle(W - 80f,  H - 140f,  60f,  50f)

        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        when (estado) {
            Estado.JUGANDO -> {
                actualizar(dt, W, H)
                dibujar(W, H)
                dibujarHUD(W, H)
                dibujarBotonPausa(W, H)
                if (bolasRestantes > 0 && !bolaActiva) dibujarBotonBolaFuego(W, H)
                gestionarToque(W, H)
            }
            Estado.PAUSADO -> {
                dibujar(W, H)
                dibujarHUD(W, H)
                dibujarMenuPausa(W, H)
                gestionarToquePausa()
            }
            Estado.ELIGIENDO -> {
                dibujarPantallaEleccion(W, H)
                gestionarToqueEleccion(W, H)
            }
            Estado.GANADO, Estado.PERDIDO -> {
                dibujar(W, H)
                dibujarHUD(W, H)
                dibujarFinJuego(W, H)
                if (Gdx.input.justTouched()) reiniciarTotal()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  INPUT JUGANDO
    // ─────────────────────────────────────────────────────────────────────────
    private fun gestionarToque(W: Float, H: Float) {
        if (!Gdx.input.justTouched()) return
        val tx = Gdx.input.x.toFloat()
        val ty = H - Gdx.input.y.toFloat()

        when {
            btnPausa.contains(tx, ty) -> {
                estado = Estado.PAUSADO
                canciones[cancionActual]?.pause()
            }
            bolasRestantes > 0 && !bolaActiva && btnBolaFuego.contains(tx, ty) -> lanzarBolaFuego()
            else -> {
                if (enSuelo || saltosRestantes > 0) {
                    velY = fuerzaSalto
                    enSuelo = false
                    saltosRestantes--
                    if (tieneUpgrade("tiempoBala")) tiempoBalaActivo = 0.5f * stacksDe("tiempoBala")
                }
            }
        }
    }

    private fun lanzarBolaFuego() {
        if (!bolaActiva && bolasRestantes > 0) {
            bolaX = truckX + truckW
            bolaY = truckY + truckH / 2
            bolaActiva = true
            bolasRestantes--
        }
    }

    private fun gestionarToquePausa() {
        if (!Gdx.input.justTouched()) return
        val H = Gdx.graphics.height.toFloat()
        val tx = Gdx.input.x.toFloat()
        val ty = H - Gdx.input.y.toFloat()
        when {
            btnContinuar.contains(tx, ty) -> { estado = Estado.JUGANDO; canciones[cancionActual]?.play() }
            btnSalir.contains(tx, ty)     -> Gdx.app.exit()
        }
    }

    private fun gestionarToqueEleccion(W: Float, H: Float) {
        if (!Gdx.input.justTouched()) return
        val tx = Gdx.input.x.toFloat()
        val ty = H - Gdx.input.y.toFloat()
        for (i in 0 until cantOpciones) {
            if (i < opcionesMejora.size && rectOpciones[i].contains(tx, ty)) {
                aplicarMejora(opcionesMejora[i])
                break
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ACTUALIZAR
    // ─────────────────────────────────────────────────────────────────────────
    private fun actualizar(dt: Float, W: Float, H: Float) {
        // Teclado
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (enSuelo || saltosRestantes > 0) {
                velY = fuerzaSalto; enSuelo = false; saltosRestantes--
                if (tieneUpgrade("tiempoBala")) tiempoBalaActivo = 0.5f * stacksDe("tiempoBala")
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && bolasRestantes > 0 && !bolaActiva) lanzarBolaFuego()
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { estado = Estado.PAUSADO; return }

        // Timers de efectos
        val effectDt = if (tiempoBalaActivo > 0) dt * 0.4f else dt
        tiempoBalaActivo = maxOf(0f, tiempoBalaActivo - dt)
        fantasmaActivo   = maxOf(0f, fantasmaActivo - dt)
        invulnerable     = maxOf(0f, invulnerable - dt)

        // Física
        velY += gravedad * effectDt
        truckY += velY * effectDt
        if (truckY <= SUELO_Y) {
            truckY = SUELO_Y; velY = 0f; enSuelo = true
            saltosRestantes = saltosMaximos  // recarga todos los saltos al tocar suelo
        }

        // Progreso
        distancia += velObst * dt * multiplicadorProgreso
        if (obstaculosSaltados >= minSaltadosDinamico) {
            canciones[cancionActual]?.pause()
            sortearMejoras()
            estado = Estado.ELIGIENDO
            return
        }

        // Bola de fuego
        if (bolaActiva) {
            bolaX += velObst * 2 * dt
            if (bolaX > W + 50) {
                bolaActiva = false
            } else {
                val bolaRect = Rectangle(bolaX - 12, bolaY - 12, 24f, 24f)
                val it = obstaculos.iterator()
                while (it.hasNext()) {
                    if (bolaRect.overlaps(it.next())) {
                        it.remove()
                        bolaActiva = false
                        break
                    }
                }
            }
        }

        // Generar obstáculos
        timerObst += dt
        if (timerObst >= intervalo) {
            timerObst = 0f
            val h = (40f + (Math.random() * 40).toFloat()) * alturaMultObst
            obstaculos.add(Rectangle(W + 10f, SUELO_Y, 40f, h))
        }

        // Colisiones
        val truck = Rectangle(truckX + 5, truckY, truckW - 10, truckH)
        val it = obstaculos.iterator()
        while (it.hasNext()) {
            val obs = it.next()
            obs.x -= velObst * effectDt
            if (obs.x + obs.width < 0) { it.remove(); obstaculosSaltados++; continue }

            val colision = invulnerable <= 0 && fantasmaActivo <= 0 && truck.overlaps(obs)
            if (colision) {
                // 1) Romper primero (usa 1 carga)
                if (rompeRestantes > 0) {
                    it.remove()
                    rompeRestantes--
                    continue
                }
                // 2) Escudo (usa 1 carga)
                if (escudosRestantes > 0) {
                    escudosRestantes--
                    invulnerable = duracionInvul
                    continue
                }
                // 3) Explosión (usa 1 carga, quita vida)
                if (explosionesRestantes > 0) {
                    obstaculos.clear()
                    explosionesRestantes--
                    vidas--
                    invulnerable = duracionInvul
                } else {
                    vidas--
                    invulnerable = duracionInvul
                }

                if (vidas <= 0) {
                    if (segundasOportRestantes > 0) {
                        vidas = 1
                        segundasOportRestantes--
                        invulnerable = 2f
                    } else {
                        estado = Estado.PERDIDO
                        canciones[cancionActual]?.stop()
                    }
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DIBUJADO
    // ─────────────────────────────────────────────────────────────────────────
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

        // Escudo visual (más grande según stacks)
        if (escudosRestantes > 0) {
            shape.color = Color(0.3f, 0.7f, 1f, 0.4f)
            shape.circle(truckX + truckW / 2, truckY + truckH / 2, truckW * 0.75f)
        }

        // Fantasma visual
        if (fantasmaActivo > 0) {
            shape.color = Color(1f, 1f, 1f, 0.3f)
            shape.rect(truckX, truckY, truckW, truckH)
        }

        // Bola de fuego
        if (bolaActiva) {
            shape.color = Color.ORANGE
            shape.circle(bolaX, bolaY, 14f)
            shape.color = Color.RED
            shape.circle(bolaX, bolaY, 8f)
            shape.color = Color.YELLOW
            shape.circle(bolaX, bolaY, 4f)
        }

        // Camión
        val parpadea = invulnerable > 0 && (invulnerable * 6).toInt() % 2 == 0
        val colorBase = when {
            fantasmaActivo > 0 -> Color(1f, 1f, 1f, 0.5f)
            slowPermActivo     -> Color.CYAN
            parpadea           -> Color.GRAY
            else               -> Color.RED
        }
        shape.color = colorBase
        shape.rect(truckX, truckY, truckW, truckH)
        shape.color = Color.FIREBRICK
        shape.rect(truckX + truckW - 30, truckY, 30f, truckH + 15)
        shape.color = Color.BLACK
        shape.circle(truckX + 20, truckY - 2, 14f)
        shape.circle(truckX + truckW - 15, truckY - 2, 14f)
        shape.color = Color.DARK_GRAY
        shape.circle(truckX + 20, truckY - 2, 8f)
        shape.circle(truckX + truckW - 15, truckY - 2, 8f)

        shape.end()
    }

    private fun dibujarHUD(W: Float, H: Float) {
        // Barra de progreso
        val progreso = minOf(obstaculosSaltados.toFloat() / minSaltadosDinamico, 1f)
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color.WHITE; shape.rect(20f, H - 40, W - 40, 18f)
        shape.color = Color.GREEN; shape.rect(20f, H - 40, (W - 40) * progreso, 18f)
        shape.end()

        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Vidas: $vidas", 20f, H - 50f)
        val pct = minOf((progreso * 100).toInt(), 100)
        font.draw(batch, "Ruta: $pct%", W / 2 - 80, H - 50f)
        font.draw(batch, "Saltos: $obstaculosSaltados/$minSaltadosDinamico", W - 220f, H - 50f)

        // Iconos de power-ups con contadores de stacks
        var iconX = 20f
        if (escudosRestantes > 0) {
            fontPequena.color = Color.CYAN
            fontPequena.draw(batch, "[ESC]x$escudosRestantes", iconX, H - 80f)
            iconX += 110f
        }
        if (bolasRestantes > 0) {
            fontPequena.color = Color.ORANGE
            fontPequena.draw(batch, "[FIRE]x$bolasRestantes [F]", iconX, H - 80f)
            iconX += 140f
        }
        if (saltosMaximos > 1) {
            fontPequena.color = Color.YELLOW
            fontPequena.draw(batch, "[DBL]x$saltosRestantes/${saltosMaximos - 1}", iconX, H - 80f)
            iconX += 120f
        }
        if (fantasmaActivo > 0) {
            fontPequena.color = Color.WHITE
            fontPequena.draw(batch, "[FTM] ${fantasmaActivo.toInt()}s", iconX, H - 80f)
            iconX += 100f
        }
        if (rompeRestantes > 0) {
            fontPequena.color = Color(1f, 0.6f, 0.2f, 1f)
            fontPequena.draw(batch, "[DRO]x$rompeRestantes", iconX, H - 80f)
            iconX += 100f
        }
        if (segundasOportRestantes > 0) {
            fontPequena.color = Color.GREEN
            fontPequena.draw(batch, "[2ND]x$segundasOportRestantes", iconX, H - 80f)
            iconX += 110f
        }
        if (explosionesRestantes > 0) {
            fontPequena.color = Color.RED
            fontPequena.draw(batch, "[EXP]x$explosionesRestantes", iconX, H - 80f)
        }

        if (rutasCompletadas > 0) {
            fontPequena.color = Color(0.7f, 0.7f, 1f, 1f)
            fontPequena.draw(batch, "Ruta #${rutasCompletadas + 1}", W - 160f, H - 80f)
        }
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

    private fun dibujarBotonBolaFuego(W: Float, H: Float) {
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0.8f, 0.3f, 0f, 0.9f)
        shape.rect(btnBolaFuego.x, btnBolaFuego.y, btnBolaFuego.width, btnBolaFuego.height)
        shape.color = Color.ORANGE
        shape.circle(btnBolaFuego.x + 30f, btnBolaFuego.y + 25f, 14f)
        shape.end()
        batch.begin()
        fontPequena.color = Color.WHITE
        fontPequena.draw(batch, "x$bolasRestantes", btnBolaFuego.x + 4f, btnBolaFuego.y + 18f)
        batch.end()
    }

    // ─── PANTALLA DE ELECCIÓN ROGUELIKE ──────────────────────────────────────
    private fun dibujarPantallaEleccion(W: Float, H: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val PADDING = 24f
        val GAP = 16f
        val cardW = minOf((W - PADDING * 2 - GAP * (cantOpciones - 1)) / cantOpciones, 240f)
        val cardH = 260f
        val totalW = cantOpciones * cardW + (cantOpciones - 1) * GAP
        val startX = W / 2f - totalW / 2f
        val cardY = H / 2f - cardH / 2f

        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0.05f, 0.05f, 0.15f, 1f)
        shape.rect(0f, 0f, W, H)

        for (i in 0 until cantOpciones) {
            val m = opcionesMejora.getOrNull(i) ?: continue
            val cx = startX + i * (cardW + GAP)
            rectOpciones[i].set(cx, cardY, cardW, cardH)

            val colorBorde = colorDeRareza(m.rareza)

            shape.color = colorBorde
            shape.rect(cx - 3f, cardY - 3f, cardW + 6f, cardH + 6f)
            shape.color = Color(0.10f, 0.10f, 0.20f, 1f)
            shape.rect(cx, cardY, cardW, cardH)
            shape.color = colorBorde
            shape.rect(cx, cardY + cardH - 28f, cardW, 28f)
        }
        shape.end()

        batch.begin()

        fontTitulo.color = Color.YELLOW
        fontTitulo.draw(batch, "ELIGE TU MEJORA", W / 2f - 190f, H - 28f)

        fontPequena.color = Color(0.65f, 0.65f, 0.8f, 1f)
        fontPequena.draw(batch, "Ruta ${rutasCompletadas + 1} completada  -  Toca una carta", W / 2f - 190f, H - 68f)

        for (i in 0 until cantOpciones) {
            val m = opcionesMejora.getOrNull(i) ?: continue
            val cx = startX + i * (cardW + GAP)
            val colorBorde = colorDeRareza(m.rareza)

            val labelRareza = m.rareza.name
            fontPequena.color = Color(0.05f, 0.05f, 0.05f, 1f)
            fontPequena.draw(batch, labelRareza, cx + 8f, cardY + cardH - 8f)

            // Mostrar cuántos stacks tiene ya el jugador de esta mejora
            val stacksActuales = stacksDe(m.id)
            fontPequena.color = colorBorde
            val tagConStack = if (stacksActuales > 0) "${m.emoji} x${stacksActuales + 1}" else m.emoji
            fontPequena.draw(batch, tagConStack, cx + 8f, cardY + cardH - 44f)

            font.color = Color.WHITE
            val nombreLineas = wrapTexto(m.nombre, cardW - 16f, 15f)
            var ny = cardY + cardH - 80f
            for (linea in nombreLineas) {
                font.draw(batch, linea, cx + 8f, ny)
                ny -= 36f
            }

            fontPequena.color = Color(0.75f, 0.75f, 0.88f, 1f)
            val descLineas = wrapTexto(m.descripcion, cardW - 16f, 12f)
            var dy = ny - 8f
            for (linea in descLineas) {
                if (dy < cardY + 8f) break
                fontPequena.draw(batch, linea, cx + 8f, dy)
                dy -= 26f
            }
        }

        // Resumen de mejoras acumuladas
        if (stacksMejoras.isNotEmpty()) {
            fontPequena.color = Color(0.45f, 0.45f, 0.65f, 1f)
            val resumen = stacksMejoras.entries.joinToString("  ") { (id, n) -> if (n > 1) "$id x$n" else id }
            fontPequena.draw(batch, resumen, PADDING, 40f)
        }

        batch.end()
    }

    private fun colorDeRareza(r: Rareza): Color = when (r) {
        Rareza.COMUN      -> Color(0.4f, 0.85f, 0.4f, 1f)
        Rareza.RARO       -> Color(0.3f, 0.5f,  1f,   1f)
        Rareza.EPICO      -> Color(0.75f, 0.2f, 1f,   1f)
        Rareza.LEGENDARIO -> Color(1f,   0.75f, 0f,   1f)
    }

    private fun wrapTexto(texto: String, anchoMax: Float, charWidth: Float): List<String> {
        val maxChars = (anchoMax / charWidth).toInt().coerceAtLeast(8)
        val palabras = texto.split(" ")
        val lineas = mutableListOf<String>()
        var linea = ""
        for (p in palabras) {
            val test = if (linea.isEmpty()) p else "$linea $p"
            if (test.length > maxChars && linea.isNotEmpty()) {
                lineas.add(linea); linea = p
            } else {
                linea = test
            }
        }
        if (linea.isNotEmpty()) lineas.add(linea)
        return lineas
    }

    private fun dibujarMenuPausa(W: Float, H: Float) {
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0f, 0f, 0f, 0.65f); shape.rect(0f, 0f, W, H)
        shape.color = Color(0.15f, 0.15f, 0.15f, 1f); shape.rect(W / 2 - 160f, H / 2 - 120f, 320f, 220f)
        shape.color = Color(0.2f, 0.75f, 0.2f, 1f); shape.rect(btnContinuar.x, btnContinuar.y, btnContinuar.width, btnContinuar.height)
        shape.color = Color(0.85f, 0.2f, 0.2f, 1f); shape.rect(btnSalir.x, btnSalir.y, btnSalir.width, btnSalir.height)
        shape.end()
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "PAUSA", W / 2 - 55f, H / 2 + 85f)
        fontPequena.color = Color.WHITE
        fontPequena.draw(batch, "Continuar", btnContinuar.x + 40f, btnContinuar.y + 38f)
        fontPequena.draw(batch, "Salir al menu", btnSalir.x + 20f, btnSalir.y + 38f)
        if (stacksMejoras.isNotEmpty()) {
            fontPequena.color = Color(0.7f, 0.7f, 1f, 1f)
            val resumen = stacksMejoras.entries.joinToString(", ") { (id, n) -> if (n > 1) "$id x$n" else id }
            fontPequena.draw(batch, resumen, W / 2 - 150f, H / 2 - 160f)
        }
        batch.end()
    }

    private fun dibujarFinJuego(W: Float, H: Float) {
        batch.begin()
        if (estado == Estado.GANADO) {
            font.color = Color.YELLOW
            font.draw(batch, "ENTREGA COMPLETADA!", W / 2 - 200, H / 2 + 30f)
            fontPequena.color = Color(0.7f, 0.7f, 0.9f, 1f)
            fontPequena.draw(batch, "Rutas completadas: $rutasCompletadas", W / 2 - 150f, H / 2 - 20f)
        } else {
            font.color = Color.RED
            font.draw(batch, "MERCANCIA DANADA!", W / 2 - 180, H / 2 + 30f)
        }
        font.color = Color.WHITE
        font.draw(batch, "Toca para jugar de nuevo", W / 2 - 200, H / 2 - 60f)
        batch.end()
    }

    private fun siguienteCancion() {
        canciones[cancionActual]?.stop()
        cancionActual = (cancionActual + 1) % canciones.size
        canciones[cancionActual]?.play()
    }

    override fun dispose() {
        shape.dispose(); batch.dispose()
        font.dispose(); fontPequena.dispose(); fontTitulo.dispose()
        musica1.dispose(); musica2.dispose()
    }

    private fun aplicarDificultadProgresiva() {
        val bonus = rutasCompletadas
        minSaltadosDinamico = 5 + bonus * 2

        // Velocidad base progresiva + reducción por stacks de velObst
        val velBase = 500f + bonus * 30f
        val factorVelObst = Math.pow(0.9, stacksDe("velObst").toDouble()).toFloat()
        velObst = velBase * factorVelObst

        // Slow perm encima de todo
        if (slowPermActivo) {
            val factorSlow = Math.pow(0.6, stacksDe("slowPerm").toDouble()).toFloat()
            velObst *= factorSlow
            gravedad *= Math.pow(0.75, stacksDe("slowPerm").toDouble()).toFloat()
        }
    }
}