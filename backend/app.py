import os
from random import choice

from flask import Flask, jsonify
from flask import request


app = Flask(__name__)

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


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)
