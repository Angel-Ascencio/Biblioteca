// Verificar sesion activa
async function verificarSesion() {
    try {
        const res = await fetch('http://localhost:8080/bibliotecaproyecto/api/acceso/sesion', {
            method: "POST"
        });
        const data = await res.json();
        if (!data.loggedIn) {
            window.location.href = 'index.html';
        }
    } catch (error) {
        console.error("Error verificando sesión:", error);
        window.location.href = 'index.html';
    }
}

verificarSesion();

let libros = [];
let currentPageLibros = 1;
const rowsPerPageLibros = 10;

//Carga los libros de acuerdo a la paginacion
function renderLibros(page = 1) {
    const start = (page - 1) * rowsPerPageLibros;
    const end = start + rowsPerPageLibros;
    const paginatedLibros = libros.slice(start, end);

    let mostrar = "";
    let checkbox = document.getElementById("chkestatus");
    const mostrarEstatus = checkbox.checked ? 0 : 1;

    for (let i = 0; i < paginatedLibros.length; i++) {
        const index = start + i;
        if (paginatedLibros[i].estatus == mostrarEstatus) {
            mostrar += '<tr>';
            mostrar += '<td>' + paginatedLibros[i].nombre_libro + '</td>';
            mostrar += '<td>' + paginatedLibros[i].autor + '</td>';
            mostrar += '<td>' + paginatedLibros[i].genero + '</td>';

            if (mostrarEstatus === 1) {
                mostrar += '<td><button class="btn btn-warning btn-modificarC" data-bs-toggle="modal" data-bs-target="#formularioModal2" onclick="modificarLibro(' + index + ');"><i class="bi bi-pencil"></i></button></td>';
                mostrar += '<td><button class="btn btn-info btn-md" onclick="mostrarLibro(' + paginatedLibros[i].id_libro + ');"><i class="bi bi-eye"></i></button></td>';
                mostrar += '<td><button class="btn btn-danger btn-md" onclick="eliminarLibro(' + index + ');"><i class="bi bi-trash"></i></button></td>';
            } else {
                mostrar += '<td><button class="btn btn-warning btn-modificarC" data-bs-toggle="modal" data-bs-target="#formularioModal2" onclick="modificarLibro(' + index + ');"><i class="bi bi-pencil"></i></button></td>';
                mostrar += '<td><button class="btn btn-info btn-md" onclick="mostrarLibro(' + paginatedLibros[i].id_libro + ');"><i class="bi bi-eye"></i></button></td>';
                mostrar += '<td><button class="btn btn-success btn-md" onclick="activarLibro(' + index + ');"><i class="bi bi-check"></i></button></td>';
            }

            mostrar += '</tr>';
        }
    }

    document.getElementById("tblLibro").innerHTML = mostrar;
    renderPaginationLibros();
}

//Controles de paginación (Anterior, Siguiente, y página actual)
function renderPaginationLibros() {
    const totalPages = Math.ceil(libros.length / rowsPerPageLibros);
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    if (totalPages <= 1)
        return;

    // Botón Anterior
    const prevBtn = document.createElement("button");
    prevBtn.classList.add("btn", "btn-secondary", "mx-1");
    prevBtn.textContent = "Anterior";
    prevBtn.disabled = currentPageLibros === 1;
    prevBtn.addEventListener("click", () => {
        if (currentPageLibros > 1) {
            currentPageLibros--;
            renderLibros(currentPageLibros);
        }
    });
    pagination.appendChild(prevBtn);

    // Indicador de pagina
    const pageIndicator = document.createElement("span");
    pageIndicator.classList.add("mx-2", "fw-bold");
    pageIndicator.textContent = `Página ${currentPageLibros} de ${totalPages}`;
    pagination.appendChild(pageIndicator);

    // Boton Siguiente
    const nextBtn = document.createElement("button");
    nextBtn.classList.add("btn", "btn-secondary", "mx-1");
    nextBtn.textContent = "Siguiente";
    nextBtn.disabled = currentPageLibros === totalPages;
    nextBtn.addEventListener("click", () => {
        if (currentPageLibros < totalPages) {
            currentPageLibros++;
            renderLibros(currentPageLibros);
        }
    });
    pagination.appendChild(nextBtn);
}

//Cargar los libros
function cargarCatLibros() {
    fetch("http://localhost:8080/bibliotecaproyecto/api/libro/getAll")
            .then(response => response.json())
            .then(response => {
                libros = response;
                //console.log("Libros cargados desde la API:", libros);
                renderLibros(currentPageLibros);
            });
}

//Carga los datos del modal cuando  se modifica
function modificarLibro(i) {
    console.log("Índice seleccionado para modificar: ", i);
    const libro = libros[i];

    if (libro) {
        //console.log("Datos del libro seleccionado: ", libro);

        document.getElementById("id").value = libro.id_libro || '';
        document.getElementById("li").value = libro.nombre_libro || '';
        document.getElementById("au").value = libro.autor || '';
        document.getElementById("es").value = libro.estatus;
        document.getElementById("ge").value = libro.genero || '';

        // lo asigna al textarea si existe el pdf
        if (libro.archivo_pdf) {
            let pdfBase64 = libro.archivo_pdf;
            // PDF tiene el prefijo de tipo de datos
            if (pdfBase64.startsWith('data:application/pdf;base64,')) {
                pdfBase64 = pdfBase64.split(',')[1]; // Elimina el prefijo
            }
            document.getElementById("link").value = pdfBase64;
        } else {
            document.getElementById("link").value = '';
        }

        console.log("Campos del modal actualizados con éxito.");
    } else {
        console.error("Error: No se encontró el libro en la posición ", i);
    }
}

//Cargar los usuarios
function mostrarLibro(id) {
    const libro = libros.find(libro => libro.id_libro === id);
    //console.log(libro);

    if (libro) {
        let pdfBase64 = libro.archivo_pdf;
        //console.log("PDF Base64:", pdfBase64);

        if (pdfBase64) {
            // Verifica si el PDF tiene el prefijo
            if (pdfBase64.startsWith('data:application/pdf;base64,')) {
                pdfBase64 = pdfBase64.split(',')[1]; // Elimina el prefijo
            }

            // Convierte el Base64 a un Blob
            const byteCharacters = atob(pdfBase64);
            const byteNumbers = new Uint8Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i); // Convierte cada carácter a un byte
            }
            const blob = new Blob([byteNumbers], {type: 'application/pdf'});
            const pdfUrl = URL.createObjectURL(blob); // Crea una URL para el Blob

            // Muestra el PDF en el iframe
            const pdfViewer = document.getElementById("pdfViewer");
            pdfViewer.src = pdfUrl;

            const modal = new bootstrap.Modal(document.getElementById('pdfModal'), {});
            modal.show();
        } else {
            alert("No hay PDF disponible para este libro.");
        }
    } else {
        console.error("No se encontró el libro con ID:", id);
    }
}

//funcion para desactivar un usuario
function eliminarLibro(i) {
    let idLibros = libros[i].id_libro;
    fetch("http://localhost:8080/bibliotecaproyecto/api/libro/delete?idLibros=" + idLibros)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error en la respuesta del servidor");
                }
                return response.json();
            })
            .then(response => {
                Swal.fire(response.result, "Libro eliminado correctamente", "success");
                selecionarLibro();
            })
            .catch(error => {
                console.error("Error al eliminar el libro:", error);
                Swal.fire("Error", "No se pudo eliminar el libro. Inténtalo de nuevo.", "error");
            });
}

//funcion para activar
function activarLibro(i) {
    let idLibros = libros[i].id_libro;
    fetch("http://localhost:8080/bibliotecaproyecto/api/libro/activar?idLibro=" + idLibros)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error en la respuesta del servidor");
                }
                return response.json();
            })
            .then(response => {
                Swal.fire(response.result, "Libro activado correctamente", "success");
                selecionarLibro();
            })
            .catch(error => {
                console.error("Error al activar el libro:", error);
                Swal.fire("Error", "No se pudo activar el libro. Inténtalo de nuevo.", "error");
            });
}

//funcion para insertar
function insertarLibro() {
    let nombreLibro = document.getElementById("libro").value;
    let autor = document.getElementById("autor").value;
    let genero = document.getElementById("genero").value;
    let estatusSelect = document.getElementById("estatus");
    let estatus = estatusSelect.value;

    // Obtener el archivo PDF
    let pdfFile = document.getElementById("pdfFile").files[0];

    // Validaciones antes de enviar
    if (!nombreLibro.trim() || !autor.trim() || !genero.trim()) {
        Swal.fire("Error", "Todos los campos son obligatorios", "warning");
        return;
    }

    if (estatus === "") {
        Swal.fire("Error", "Debes seleccionar un estatus", "warning");
        return;
    }

    if (!pdfFile) {
        Swal.fire("Archivo PDF requerido", "Por favor, selecciona un archivo PDF.", "warning");
        return;
    }

    // Leer el archivo PDF y convertirlo a Base64
    let reader = new FileReader();
    reader.onloadend = function () {
        let base64String = reader.result.split(',')[1];

        let libro = {
            nombre_libro: nombreLibro,
            autor: autor,
            genero: genero,
            estatus: parseInt(estatus),
            archivo_pdf: base64String
        };

        let params = {l: JSON.stringify(libro)};

        let ruta = "http://localhost:8080/bibliotecaproyecto/api/libro/insert";
        fetch(ruta, {
            method: "POST",
            headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
            body: new URLSearchParams(params)
        })
                .then(response => response.json())
                .then(response => {
                    if (response.result) {
                        Swal.fire("Inserción de libro correcta", response.result, "success");
                        recargarTabla();
                        cerrarModalInsert();
                    } else if (response.error) {
                        Swal.fire("Problemas para insertar el libro", response.error, "error");
                    }
                })
                .catch(error => {
                    Swal.fire("Error", error.message, "error");
                });
    };

    reader.readAsDataURL(pdfFile);
}

// Función para convertir el archivo PDF seleccionado a Base64 y actualizar el textarea
document.getElementById("pdf").addEventListener("change", function (event) {
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();

        // Leer el archivo como Base64
        reader.onloadend = function () {
            const base64String = reader.result;

            // Eliminar el prefijo 'data:application/pdf;base64,'
            const base64WithoutPrefix = base64String.split(',')[1];
            document.getElementById("link").value = base64WithoutPrefix;
            //console.log("PDF convertido a Base64 sin prefijo:", base64WithoutPrefix);

            // Convertir Base64 a Blob
            const blob = base64ToBlob(base64String, 'application/pdf');
            //console.log("PDF convertido a Blob:", blob);
        };

        reader.readAsDataURL(file);
    }
});

// Función para convertir Base64 a Blob
function base64ToBlob(base64, type) {
    const byteCharacters = atob(base64.split(',')[1]);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], {type: type});
}

// Función para modificar el libro y actualizar el PDF en Base64
function moddLibro() {
    let idLibros = document.getElementById("id").value;
    let nombreLibro = document.getElementById("li").value;
    let autor = document.getElementById("au").value;
    let genero = document.getElementById("ge").value;
    let estatus = document.getElementById("es").value;
    let pdfBase64 = document.getElementById("link").value;

    if (!autor.trim()) {
        Swal.fire("Error", "Todos los campos son obligatorios", "warning");
        return;
    }

    let libro = {
        id_libro: parseInt(idLibros),
        nombre_libro: nombreLibro,
        autor: autor,
        genero: genero,
        estatus: parseInt(estatus),
        archivo_pdf: pdfBase64
    };

    let params = JSON.stringify(libro);

    let ruta = "http://localhost:8080/bibliotecaproyecto/api/libro/update";

    fetch(ruta, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: params
    })
            .then(response => response.json())
            .then(response => {
                if (response.result) {
                    Swal.fire("Actualización de libro correcta", response.result, "success");
                    recargarTabla();
                    cerrarModalupdate();
                }
                if (response.error) {
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: "Hubo un problema al actualizar el libro. Por favor, intenta nuevamente."
                    });
                }
                cargarCatalogoLibros();
            });

    limpiarCampo();
}

//funcion para activos/inactivos
function selecionarLibro() {
    let checkbox = document.getElementById("chkestatus");
    const val = checkbox.checked ? 0 : 1;

    fetch(`http://localhost:8080/bibliotecaproyecto/api/libro/seleccionar?val=${val}`)
            .then(response => response.json())
            .then(response => {
                libros = response;
                currentPageLibros = 1;
                renderLibros(currentPageLibros);
            })
            .catch(error => console.error("Error al cargar libros:", error));
}

//funcion para buscar
function buscarLibro() {
    let busqueda = document.getElementById("campoBusqueda").value.trim();
    if (busqueda === "")
        return;

    let checkbox = document.getElementById("chkestatus");
    let estatusFiltro = checkbox.checked ? 0 : 1;

    fetch(`http://localhost:8080/bibliotecaproyecto/api/libro/buscar?valor=${busqueda}`)
            .then(response => response.json())
            .then(response => {
                // Filtra segun el estatus
                libros = response.filter(libro => libro.estatus === estatusFiltro);

                currentPageLibros = 1;

                renderLibros(currentPageLibros);
            })
            .catch(error => console.error("Error al buscar libros:", error));
}

//funcion para limpiar los campos
function limpiarCampo() {
    var nombreLibro = document.getElementById('libro');
    var autor = document.getElementById('autor');
    var genero = document.getElementById('genero');
    var estatus = document.getElementById('estatus');
    var pdf = document.getElementById('pdfFile');

    nombreLibro.value = '';
    autor.value = '';
    genero.value = '';
    estatus.value = '';
    genero.value = '';
    estatus.value = '';
    pdf.value = '';
}

//funcion para recargar la tabla
function recargarTabla() {
    cargarCatLibros();
}

//funcion para cerrar el modal de actualizar
function cerrarModalupdate() {
    const modal = document.getElementById('formularioModal2');
    const modalInstance = bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
    modalInstance.hide();
}

//funcion para cerrar el modal de insertar
function cerrarModalInsert() {
    const modal = document.getElementById('formularioModal1');
    const modalInstance = bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
    modalInstance.hide();
}

// Funcion para cerrar sesión
async function cerrarSesion() {
    try {
        await fetch('http://localhost:8080/bibliotecaproyecto/api/acceso/logout', {method: "POST"});
        window.location.href = 'index.html';
    } catch (error) {
        console.error("Error cerrando sesion:", error);
        Swal.fire("Error", "No se pudo cerrar sesion.", "error");
    }
}

//Para el boton de cerrar sesión
document.getElementById("btnCerrarSesion").addEventListener("click", function () {
    cerrarSesion();
});
