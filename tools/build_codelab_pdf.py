from pathlib import Path
import textwrap

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (
    PageBreak,
    Paragraph,
    Preformatted,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "RickMortyExplorer_Codelab_Academico.pdf"

ACCENT = colors.HexColor("#167A72")
ACCENT_DARK = colors.HexColor("#0D4F4A")
MUTED = colors.HexColor("#667085")
LIGHT = colors.HexColor("#E6F4F1")
BORDER = colors.HexColor("#D0D5DD")
CODE_BG = colors.HexColor("#F4F6F8")


def read(relative):
    return (ROOT / relative).read_text(encoding="utf-8")


def esc(text):
    return (
        text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\n", "<br/>")
    )


def wrap_code(code, width=94, max_lines=None):
    lines = []
    for line in code.strip().splitlines():
        if not line:
            lines.append("")
            continue
        chunks = textwrap.wrap(
            line,
            width=width,
            replace_whitespace=False,
            drop_whitespace=False,
            break_long_words=False,
        )
        lines.extend(chunks or [""])
    if max_lines and len(lines) > max_lines:
        lines = lines[:max_lines] + ["..."]
    return "\n".join(lines)


def code_block(code, max_lines=None):
    return Preformatted(wrap_code(code, max_lines=max_lines), STYLES["Code"])


def note(title, body):
    return Table(
        [[Paragraph(f"<b>{esc(title)}</b><br/>{esc(body)}", STYLES["Body"])]],
        colWidths=[17.1 * cm],
        style=TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), LIGHT),
                ("BOX", (0, 0), (-1, -1), 0.6, colors.HexColor("#B6E2D9")),
                ("LEFTPADDING", (0, 0), (-1, -1), 10),
                ("RIGHTPADDING", (0, 0), (-1, -1), 10),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
            ]
        ),
    )


def heading(text, level=1):
    return Paragraph(esc(text), STYLES[f"H{level}"])


def para(text):
    return Paragraph(esc(text), STYLES["Body"])


def bullets(items):
    story = []
    for item in items:
        story.append(Paragraph("• " + esc(item), STYLES["Bullet"]))
    return story


def req_table():
    data = [
        ["Requisito", "Implementacion", "Estado"],
        ["Java + Views/XML", "Activities en Java y layouts XML", "Cumplido"],
        ["Retrofit + Gson + RxJava2", "RetrofitClient, GsonConverterFactory y RxJava2CallAdapterFactory", "Cumplido"],
        ["RecyclerView", "CharacterAdapter con CharacterViewHolder", "Cumplido"],
        ["Endpoint lista", "GET /api/character?page=1", "Cumplido"],
        ["Endpoint detalle", "GET /api/character/{id}", "Cumplido"],
        ["Loading y error", "ProgressBar, contenedor de error y boton de reintento", "Cumplido"],
    ]
    data = [[Paragraph(esc(cell), STYLES["TableCell"]) for cell in row] for row in data]
    table = Table(data, colWidths=[4.2 * cm, 9.1 * cm, 3.2 * cm], repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#D7F0EB")),
                ("TEXTCOLOR", (0, 0), (-1, 0), ACCENT_DARK),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.4, BORDER),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("LEFTPADDING", (0, 0), (-1, -1), 7),
                ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    return table


def errors_table():
    rows = [
        ["Problema", "Solucion"],
        ["La app no tiene Internet", "Comprobar el permiso INTERNET en AndroidManifest.xml."],
        ["No aparecen los bindings", "Verificar que viewBinding = true y que los ids existen en XML."],
        ["Error de Gradle", "Sincronizar proyecto y revisar versiones de Gradle/AGP."],
        ["No carga la API", "Comprobar conexion del emulador y URL base de Retrofit."],
        ["No abre detalle", "Comprobar que se envia EXTRA_CHARACTER_ID en el Intent."],
    ]
    data = [[Paragraph(esc(cell), STYLES["TableCell"]) for cell in row] for row in rows]
    table = Table(data, colWidths=[6 * cm, 10.5 * cm], repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#D7F0EB")),
                ("GRID", (0, 0), (-1, -1), 0.4, BORDER),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("LEFTPADDING", (0, 0), (-1, -1), 7),
                ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
            ]
        )
    )
    return table


def page_footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(colors.HexColor("#E4E7EC"))
    canvas.line(1.8 * cm, 1.35 * cm, 19.2 * cm, 1.35 * cm)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(MUTED)
    canvas.drawString(1.8 * cm, 0.9 * cm, "RickMorty Explorer · Codelab academico")
    canvas.drawRightString(19.2 * cm, 0.9 * cm, f"Pagina {doc.page}")
    canvas.restoreState()


def build_story():
    story = []
    story.append(Spacer(1, 2.6 * cm))
    story.append(Paragraph("RickMorty Explorer", STYLES["CoverTitle"]))
    story.append(Paragraph("Codelab completo y memoria tecnica de apoyo", STYLES["CoverSub"]))
    story.append(
        Paragraph(
            "Proyecto Android en Java + XML con Retrofit, Gson, RxJava2 y RecyclerView",
            STYLES["CoverMeta"],
        )
    )
    story.append(Spacer(1, 0.6 * cm))
    cover_data = [
        ["Asignatura", "Computacion Movil"],
        ["Proyecto", "RickMortyExplorer"],
        ["API REST", "Rick and Morty API"],
        ["Tecnologias", "Java, XML, Retrofit, Gson, RxJava2, RecyclerView, Picasso"],
        ["Fecha", "Mayo de 2026"],
    ]
    table = Table(
        [[Paragraph(esc(a), STYLES["TableCellBold"]), Paragraph(esc(b), STYLES["TableCell"])] for a, b in cover_data],
        colWidths=[4.2 * cm, 9.8 * cm],
    )
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#D7F0EB")),
                ("GRID", (0, 0), (-1, -1), 0.4, BORDER),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("LEFTPADDING", (0, 0), (-1, -1), 8),
                ("RIGHTPADDING", (0, 0), (-1, -1), 8),
                ("TOPPADDING", (0, 0), (-1, -1), 7),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
            ]
        )
    )
    story.append(table)
    story.append(PageBreak())

    story.append(heading("Indice", 1))
    for item in [
        "1. Objetivo del proyecto",
        "2. Requisitos cubiertos",
        "3. API utilizada",
        "4. Estructura del proyecto",
        "5. Configuracion Gradle",
        "6. AndroidManifest",
        "7. Recursos XML y layouts",
        "8. Modelos Java",
        "9. Retrofit y endpoints",
        "10. RecyclerView: Adapter y ViewHolder",
        "11. MainActivity",
        "12. DetailActivity",
        "13. Flujo completo de la aplicacion",
        "14. Estados de loading y error",
        "15. Errores tipicos y solucion",
        "16. Pruebas realizadas",
        "17. Posibles ampliaciones",
        "18. Guia de defensa",
    ]:
        story += bullets([item])
    story.append(PageBreak())

    story.append(heading("1. Objetivo del proyecto", 1))
    story.append(
        para(
            "El objetivo de este proyecto es desarrollar una aplicacion Android completa, sencilla y defendible que consuma una API REST externa y muestre informacion en una interfaz construida con Java y XML."
        )
    )
    story.append(
        note(
            "Idea principal",
            "RickMorty Explorer muestra una lista de personajes de la Rick and Morty API. Al seleccionar un personaje, se abre una pantalla de detalle con datos ampliados.",
        )
    )
    story.append(Spacer(1, 0.2 * cm))

    story.append(heading("2. Requisitos cubiertos", 1))
    story.append(req_table())
    story.append(Spacer(1, 0.2 * cm))

    story.append(heading("3. API utilizada", 1))
    story.append(para("Se utiliza la Rick and Morty API, una API publica sin clave de acceso."))
    story.append(
        code_block(
            """
Base URL: https://rickandmortyapi.com/api/
Lista:    GET character?page=1
Detalle:  GET character/{id}
            """
        )
    )
    story.append(
        para(
            "La respuesta de lista contiene informacion de paginacion y un array results. La respuesta de detalle devuelve un unico personaje con campos como id, name, status, species, gender, origin, location e image."
        )
    )

    story.append(heading("4. Estructura del proyecto", 1))
    story.append(
        code_block(
            """
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
            """
        )
    )
    story.append(
        para(
            "Esta separacion ayuda a que cada parte tenga una responsabilidad clara: modelos para datos, rest para red, ui para pantallas, adapter para RecyclerView y util para formateo."
        )
    )

    story.append(heading("5. Configuracion Gradle", 1))
    story.append(para("El proyecto usa Gradle con Version Catalogs para centralizar versiones."))
    story.append(code_block(read("gradle/libs.versions.toml"), max_lines=42))
    story.append(para("En el modulo app se activa ViewBinding y se declaran las dependencias principales."))
    story.append(code_block(read("app/build.gradle.kts"), max_lines=52))

    story.append(heading("6. AndroidManifest", 1))
    story.append(
        para(
            "El Manifest declara el permiso de Internet, necesario para consumir la API REST, y registra las dos actividades de la aplicacion."
        )
    )
    story.append(code_block(read("app/src/main/AndroidManifest.xml"), max_lines=36))

    story.append(heading("7. Recursos XML y layouts", 1))
    story.append(heading("7.1 Pantalla principal", 2))
    story.append(
        para(
            "El layout activity_main.xml contiene una cabecera, un RecyclerView, un ProgressBar y un contenedor de error con boton de reintento."
        )
    )
    story.append(code_block(read("app/src/main/res/layout/activity_main.xml"), max_lines=70))
    story.append(heading("7.2 Fila del RecyclerView", 2))
    story.append(
        para("La fila row_character.xml muestra imagen, nombre, especie, genero y estado del personaje.")
    )
    story.append(code_block(read("app/src/main/res/layout/row_character.xml"), max_lines=62))
    story.append(heading("7.3 Pantalla de detalle", 2))
    story.append(
        para(
            "El layout activity_detail.xml repite el mismo enfoque de estados: contenido, carga y error."
        )
    )

    story.append(heading("8. Modelos Java", 1))
    story.append(
        para(
            "Los modelos son POJOs que coinciden con el JSON devuelto por la API. Gson rellena estos objetos automaticamente."
        )
    )
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/model/CharacterItem.java"), max_lines=72))
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/model/CharacterListResponse.java"), max_lines=24))

    story.append(heading("9. Retrofit y endpoints", 1))
    story.append(
        para(
            "RetrofitClient centraliza la URL base y registra Gson y RxJava2. RickMortyApiService define los endpoints mediante anotaciones."
        )
    )
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/rest/RetrofitClient.java"), max_lines=42))
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/rest/api/RickMortyApiService.java"), max_lines=32))

    story.append(heading("10. RecyclerView: Adapter y ViewHolder", 1))
    story.append(
        para(
            "El adaptador recibe una lista de personajes y se encarga de pintar cada fila. Tambien define una interfaz de click para abrir el detalle."
        )
    )
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/ui/adapter/CharacterAdapter.java"), max_lines=92))

    story.append(heading("11. MainActivity", 1))
    story.append(
        para(
            "La pantalla principal crea el servicio Retrofit, configura el RecyclerView y llama al endpoint de lista. La peticion se ejecuta en segundo plano con Schedulers.io() y vuelve al hilo principal con AndroidSchedulers.mainThread()."
        )
    )
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/ui/MainActivity.java"), max_lines=86))

    story.append(heading("12. DetailActivity", 1))
    story.append(para("La pantalla de detalle recibe el id del personaje por Intent y consume el endpoint de detalle."))
    story.append(code_block(read("app/src/main/java/com/example/rickmortyexplorer/ui/DetailActivity.java"), max_lines=98))

    story.append(heading("13. Flujo completo de la aplicacion", 1))
    story += bullets(
        [
            "El usuario abre la app.",
            "MainActivity muestra un ProgressBar.",
            "Se llama a GET character?page=1.",
            "Cuando llegan los datos, el RecyclerView muestra los personajes.",
            "El usuario pulsa un personaje.",
            "DetailActivity recibe el id por Intent.",
            "Se llama a GET character/{id}.",
            "La pantalla de detalle muestra la informacion ampliada.",
        ]
    )

    story.append(heading("14. Estados de loading y error", 1))
    story.append(
        para(
            "La app no deja la pantalla vacia durante las peticiones. Tanto la lista como el detalle tienen funciones para mostrar carga, contenido o error."
        )
    )
    story.append(
        code_block(
            """
showLoading()  -> muestra ProgressBar y oculta contenido/error
showContent()  -> muestra la informacion cargada
showError()    -> muestra mensaje de error y boton de reintento
            """
        )
    )

    story.append(heading("15. Errores tipicos y solucion", 1))
    story.append(errors_table())

    story.append(heading("16. Pruebas realizadas", 1))
    story += bullets(
        [
            "Sincronizacion Gradle correcta.",
            "Compilacion debug correcta.",
            "Ejecucion en emulador Android.",
            "Carga de lista de personajes.",
            "Scroll en RecyclerView.",
            "Apertura de pantalla de detalle al pulsar un personaje.",
        ]
    )

    story.append(heading("17. Posibles ampliaciones", 1))
    story += bullets(
        [
            "Anadir buscador por nombre.",
            "Anadir paginacion para cargar mas personajes.",
            "Guardar favoritos localmente.",
            "Migrar a MVVM.",
            "Usar fragments para navegacion adaptable.",
            "Mostrar localizaciones o episodios como pantallas adicionales.",
        ]
    )

    story.append(heading("18. Guia de defensa", 1))
    story.append(para("Para defender el proyecto se recomienda explicar el flujo con este orden:"))
    story += bullets(
        [
            "Primero, explicar la API y los dos endpoints usados.",
            "Despues, ensenar RetrofitClient y RickMortyApiService.",
            "A continuacion, mostrar MainActivity y el RecyclerView.",
            "Luego, abrir CharacterAdapter para identificar Adapter y ViewHolder.",
            "Por ultimo, mostrar DetailActivity y explicar el segundo endpoint.",
        ]
    )
    story.append(
        note(
            "Resumen final",
            "RickMorty Explorer cumple los requisitos minimos del proyecto final y queda preparado para ampliaciones opcionales sin cambiar la base arquitectonica.",
        )
    )
    return story


styles = getSampleStyleSheet()
STYLES = {
    "CoverTitle": ParagraphStyle(
        "CoverTitle",
        parent=styles["Title"],
        fontName="Helvetica-Bold",
        fontSize=30,
        leading=35,
        alignment=TA_CENTER,
        textColor=ACCENT_DARK,
        spaceAfter=10,
    ),
    "CoverSub": ParagraphStyle(
        "CoverSub",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=15,
        leading=19,
        alignment=TA_CENTER,
        textColor=ACCENT,
        spaceAfter=8,
    ),
    "CoverMeta": ParagraphStyle(
        "CoverMeta",
        parent=styles["Normal"],
        fontSize=10.5,
        leading=14,
        alignment=TA_CENTER,
        textColor=MUTED,
        spaceAfter=20,
    ),
    "H1": ParagraphStyle(
        "H1",
        parent=styles["Heading1"],
        fontName="Helvetica-Bold",
        fontSize=17,
        leading=21,
        textColor=ACCENT_DARK,
        spaceBefore=12,
        spaceAfter=7,
        keepWithNext=True,
    ),
    "H2": ParagraphStyle(
        "H2",
        parent=styles["Heading2"],
        fontName="Helvetica-Bold",
        fontSize=12.5,
        leading=16,
        textColor=ACCENT,
        spaceBefore=8,
        spaceAfter=5,
        keepWithNext=True,
    ),
    "Body": ParagraphStyle(
        "Body",
        parent=styles["BodyText"],
        fontName="Helvetica",
        fontSize=9.8,
        leading=13.2,
        alignment=TA_LEFT,
        spaceAfter=6,
    ),
    "Bullet": ParagraphStyle(
        "Bullet",
        parent=styles["BodyText"],
        fontName="Helvetica",
        fontSize=9.8,
        leading=13.2,
        leftIndent=10,
        firstLineIndent=-8,
        spaceAfter=4,
    ),
    "Code": ParagraphStyle(
        "Code",
        parent=styles["Code"],
        fontName="Courier",
        fontSize=7.2,
        leading=8.6,
        textColor=colors.HexColor("#172026"),
        backColor=CODE_BG,
        borderColor=colors.HexColor("#E4E7EC"),
        borderWidth=0.4,
        borderPadding=5,
        spaceBefore=3,
        spaceAfter=7,
    ),
    "TableCell": ParagraphStyle(
        "TableCell",
        parent=styles["BodyText"],
        fontName="Helvetica",
        fontSize=8.9,
        leading=11.5,
    ),
    "TableCellBold": ParagraphStyle(
        "TableCellBold",
        parent=styles["BodyText"],
        fontName="Helvetica-Bold",
        fontSize=8.9,
        leading=11.5,
        textColor=ACCENT_DARK,
    ),
}


def main():
    doc = SimpleDocTemplate(
        str(OUT),
        pagesize=A4,
        rightMargin=1.9 * cm,
        leftMargin=1.9 * cm,
        topMargin=1.75 * cm,
        bottomMargin=1.65 * cm,
        title="RickMorty Explorer Codelab Academico",
        author="RickMortyExplorer",
    )
    doc.build(build_story(), onFirstPage=page_footer, onLaterPages=page_footer)
    print(OUT)


if __name__ == "__main__":
    main()
