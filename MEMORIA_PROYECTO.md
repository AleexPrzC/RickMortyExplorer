# Memoria tecnica - RickMorty Explorer

## 1. Introduccion

RickMorty Explorer es una aplicacion Android desarrollada en Java y XML para consultar informacion de personajes desde una API REST publica. El objetivo principal es aplicar los contenidos del modulo de Computacion Movil: consumo de servicios web, listas dinamicas, navegacion entre pantallas y gestion basica de estados.

## 2. API utilizada

La aplicacion utiliza la Rick and Morty API:

- Base URL: `https://rickandmortyapi.com/api/`
- Endpoint de lista: `GET character?page=1`
- Endpoint de detalle: `GET character/{id}`

El endpoint de lista devuelve un objeto JSON con informacion de paginacion y un array `results`. El endpoint de detalle devuelve un unico personaje con campos como `id`, `name`, `status`, `species`, `gender`, `origin`, `location` e `image`.

## 3. Tecnologias utilizadas

- Android Studio.
- Java.
- XML Views.
- Retrofit para realizar peticiones HTTP.
- Gson para convertir JSON en objetos Java.
- RxJava2 y RxAndroid para ejecutar peticiones de forma asincrona.
- RecyclerView para mostrar la lista.
- Picasso para cargar imagenes desde URL.
- ViewBinding para acceder a las vistas de forma segura.

## 4. Requisitos minimos cubiertos

La aplicacion cumple los requisitos minimos del proyecto final:

- Esta desarrollada en Java usando Views/XML.
- Consume una API REST externa.
- Usa Retrofit, Gson y RxJava2.
- Incluye una pantalla principal con RecyclerView, Adapter y ViewHolder.
- Consume dos endpoints: uno de lista y otro de detalle.
- Incluye estados de loading y error.

## 5. Estructura de paquetes

```text
model: clases que representan los datos JSON.
rest: configuracion de Retrofit.
rest.api: interfaz de endpoints.
ui: actividades principales.
ui.adapter: adaptador y ViewHolder del RecyclerView.
util: funciones auxiliares de formato.
```

## 6. Flujo de funcionamiento

Al abrir la app, `MainActivity` muestra un estado de carga y llama a `getCharacters(1)`. Cuando la respuesta llega correctamente, se oculta el progreso y se muestra la lista de personajes en el RecyclerView. Si hay un error de red, aparece un mensaje y un boton para reintentar.

Cuando el usuario pulsa sobre un personaje, se abre `DetailActivity` mediante un Intent que contiene el id del personaje. La pantalla de detalle llama a `getCharacterDetail(id)` y muestra la imagen, el nombre, el estado, especie, genero, origen y ubicacion.

## 7. Manejo de errores y loading

La pantalla principal y la pantalla de detalle tienen tres estados:

- Loading: se muestra un `ProgressBar`.
- Content: se muestra la informacion cargada.
- Error: se muestra un mensaje y un boton de reintento.

Esto permite que la aplicacion no se quede bloqueada ni vacia si falla la conexion.

## 8. Posibles mejoras

- Anadir paginacion para cargar mas personajes.
- Anadir buscador por nombre.
- Guardar favoritos de forma local con SharedPreferences o Room.
- Separar la logica con MVVM.
- Usar fragments para adaptar mejor la navegacion.
