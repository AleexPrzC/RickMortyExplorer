import os
from random import choice

from flask import Flask, Response, jsonify
from flask import request


app = Flask(__name__)

GITHUB_REPO_URL = "https://github.com/AleexPrzC/RickMortyExplorer"
GITHUB_SOURCE_ZIP_URL = f"{GITHUB_REPO_URL}/archive/refs/heads/main.zip"
GITHUB_RELEASES_URL = f"{GITHUB_REPO_URL}/releases"

CHARACTER_TIPS = [
    {
        "title": "Consejo interdimensional",
        "message": "Guarda como favoritos los personajes que quieras revisar sin perderlos entre portales.",
    },
    {
        "title": "Explorador atento",
        "message": "Revisa el estado del personaje antes de anadirlo a favoritos: vivo, muerto o desconocido.",
    },
    {
        "title": "Dato de la Ciudadela",
        "message": "Si estas sin conexion, consulta la ultima lista descargada y vuelve a sincronizar despues.",
    },
    {
        "title": "Recomendacion de viaje",
        "message": "Compara especie, origen y ubicacion para descubrir patrones entre dimensiones.",
    },
]


@app.get("/health")
def health():
    return jsonify(
        {
            "status": "ok",
            "service": "rickmorty-backend",
        }
    )


@app.get("/")
def index():
    return jsonify(
        {
            "service": "rickmorty-backend",
            "endpoints": ["/health", "/character-tip", "/favorite-summary?count=3", "/download"],
        }
    )


@app.get("/character-tip")
def character_tip():
    tip = choice(CHARACTER_TIPS)
    return jsonify(tip)


@app.get("/favorite-summary")
def favorite_summary():
    count = request.args.get("count", default=0, type=int)
    if count == 0:
        message = "Todavia no hay favoritos guardados para tus viajes interdimensionales."
    elif count == 1:
        message = "Tienes 1 personaje favorito listo para consultar despues."
    else:
        message = f"Tienes {count} personajes favoritos guardados para tus viajes interdimensionales."

    return jsonify(
        {
            "title": "Resumen de favoritos",
            "message": message,
            "count": count,
        }
    )


@app.get("/download")
def download():
    html = f"""<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>RickMortyExplorer CUYN</title>
    <style>
        body {{ font-family: Arial, sans-serif; max-width: 760px; margin: 48px auto; padding: 0 20px; line-height: 1.5; }}
        h1 {{ color: #0d4f4a; }}
        a {{ color: #167a72; font-weight: 700; }}
        .card {{ border: 1px solid #d0d5dd; border-radius: 8px; padding: 16px; margin: 16px 0; }}
        code {{ background: #f2f4f7; padding: 2px 6px; border-radius: 4px; }}
    </style>
</head>
<body>
    <h1>RickMortyExplorer CUYN</h1>
    <p>Backend Flask desplegado en Render. Desde aqui puedes acceder al codigo fuente y a los artefactos de entrega.</p>

    <div class="card">
        <h2>Descargar APK</h2>
        <p>El APK debug se publica como archivo adjunto en GitHub Releases.</p>
        <p><a href="{GITHUB_RELEASES_URL}">Abrir releases del proyecto</a></p>
    </div>

    <div class="card">
        <h2>Codigo fuente</h2>
        <p><a href="{GITHUB_REPO_URL}">Abrir repositorio en GitHub</a></p>
        <p><a href="{GITHUB_SOURCE_ZIP_URL}">Descargar ZIP de la rama main</a></p>
        <p>Nota: <code>app/google-services.json</code> no se incluye en el repositorio publico por seguridad.</p>
    </div>

    <div class="card">
        <h2>Endpoints</h2>
        <p><a href="/health">/health</a></p>
        <p><a href="/character-tip">/character-tip</a></p>
        <p><a href="/favorite-summary?count=3">/favorite-summary?count=3</a></p>
    </div>
</body>
</html>"""
    return Response(html, mimetype="text/html")


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)
