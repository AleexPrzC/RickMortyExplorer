# RickMorty Explorer

Proyecto final de Computacion Movil desarrollado en Android con Java, Views/XML, Retrofit, Gson, RxJava2 y RecyclerView.

## Idea de la app

La aplicacion muestra personajes de la API publica de Rick and Morty. La pantalla principal carga una lista de personajes y, al pulsar sobre uno, abre una pantalla de detalle con mas informacion.

API usada: https://rickandmortyapi.com/documentation

## Requisitos cubiertos

- App Android en Java con interfaces XML.
- Consumo de API REST con Retrofit.
- Conversion JSON con Gson.
- Peticiones asincronas con RxJava2 y RxAndroid.
- Pantalla principal con RecyclerView, Adapter y ViewHolder.
- Dos endpoints:
  - Lista: `GET /api/character?page=1`
  - Detalle: `GET /api/character/{id}`
- Estados basicos de loading y error en lista y detalle.
- Navegacion entre pantallas mediante Intent.
- Carga de imagenes con Picasso.
- ViewBinding activado.

## Estructura del proyecto

```text
com.example.rickmortyexplorer
|-- model
|   |-- CharacterItem.java
|   |-- CharacterListResponse.java
|   |-- Info.java
|   `-- Origin.java
|-- rest
|   |-- RetrofitClient.java
|   `-- api
|       `-- RickMortyApiService.java
|-- ui
|   |-- MainActivity.java
|   |-- DetailActivity.java
|   `-- adapter
|       `-- CharacterAdapter.java
`-- util
    `-- CharacterUtils.java
```

## Como abrirlo desde cero en Android Studio

1. Abre Android Studio.
2. Selecciona `Open`.
3. Elige la carpeta `C:\Universidad\Cuarto\CUMN\RickMortyExplorer`.
4. Espera a que Gradle sincronice.
5. Si Android Studio pide instalar SDK, acepta la instalacion recomendada.
6. Ejecuta la app con el boton Run sobre un emulador o movil Android.

## Puntos importantes para defender

- `RetrofitClient` centraliza la URL base y crea Retrofit con Gson y RxJava2.
- `RickMortyApiService` declara los endpoints con anotaciones `@GET`, `@Query` y `@Path`.
- `MainActivity` llama al endpoint de lista y actualiza el RecyclerView.
- `CharacterAdapter` contiene el ViewHolder y pinta cada fila.
- `DetailActivity` recibe el id por Intent y llama al endpoint de detalle.
- `CompositeDisposable` evita dejar suscripciones RxJava activas al cerrar pantallas.

## Nota de compilacion local

En esta maquina la compilacion por consola no puede finalizar porque no hay Android SDK configurado. El error esperado es:

```text
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable
or by setting the sdk.dir path in local.properties.
```

Android Studio suele resolverlo al abrir el proyecto e instalar/configurar el SDK.
