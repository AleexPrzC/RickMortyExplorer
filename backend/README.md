# RickMorty Backend

Backend propio para el modulo de Computacion en la Nube y Computacion Ubicua.

## Endpoints

- `GET /health`: comprueba que el servicio esta activo.
- `GET /character-tip`: devuelve un consejo tematico de Rick and Morty en formato JSON.
- `GET /favorite-summary?count=3`: devuelve un resumen tematico segun el numero de favoritos.

## Ejecutar con Python

```bash
pip install -r requirements.txt
python app.py
```

## Ejecutar con Docker

```bash
docker build -t rickmorty-backend .
docker run --rm -p 5000:5000 rickmorty-backend
```

## Probar

```bash
curl http://localhost:5000/health
curl http://localhost:5000/character-tip
curl "http://localhost:5000/favorite-summary?count=3"
```

Desde el emulador Android, el backend local del ordenador se accede como:

```text
http://10.0.2.2:5000
```
