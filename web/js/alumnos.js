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
let librosFiltrados = [];
let currentPageLibro = 1;
const rowsPerPageLibro = 10;

//Carga los libros de acuerdo a la paginacion
function renderTableLibros(page = 1) {
    const start = (page - 1) * rowsPerPageLibro;
    const end = start + rowsPerPageLibro;
    const paginatedLibros = librosFiltrados.slice(start, end); // Obtiene solo los libros de esa página

    let mostrar = "";

    for (let i = 0; i < paginatedLibros.length; i++) {
        if (paginatedLibros[i].estatus == 1) { // solo activos
            mostrar += '<tr>';
            mostrar += '<td>' + paginatedLibros[i].nombre + '</td>';
            mostrar += '<td>' + paginatedLibros[i].autor + '</td>';
            mostrar += '<td>' + paginatedLibros[i].genero + '</td>';
            mostrar += '<td>' + paginatedLibros[i].universidad + '</td>';
            mostrar += '<td> <button class="btn btn-info btn-md" onclick="mostrarLibro(' + libros.indexOf(paginatedLibros[i]) + ');"><i class="bi bi-eye"></i></button>' + '</td>';
            mostrar += '</tr>';
        }
    }

    document.getElementById("tblLibro").innerHTML = mostrar;
    renderPaginationLibros();// Actualiza los controles de paginación
}

//Controles de paginación (Anterior, Siguiente, y página actual)
function renderPaginationLibros() {
    const totalPages = Math.ceil(librosFiltrados.length / rowsPerPageLibro);
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";
    //Si es solo una pagina no se muestran
    if (totalPages <= 1)
        return;

    const prevBtn = document.createElement("button");
    prevBtn.classList.add("btn", "btn-secondary", "mx-1");
    prevBtn.textContent = "Anterior";
    prevBtn.disabled = currentPageLibro === 1;
    prevBtn.addEventListener("click", () => {
        if (currentPageLibro > 1) {
            currentPageLibro--;
            renderTableLibros(currentPageLibro);
        }
    });
    pagination.appendChild(prevBtn);

    const pageIndicator = document.createElement("span");
    pageIndicator.classList.add("mx-2", "fw-bold");
    pageIndicator.textContent = `Página ${currentPageLibro} de ${totalPages}`;
    pagination.appendChild(pageIndicator);

    const nextBtn = document.createElement("button");
    nextBtn.classList.add("btn", "btn-secondary", "mx-1");
    nextBtn.textContent = "Siguiente";
    nextBtn.disabled = currentPageLibro === totalPages;
    nextBtn.addEventListener("click", () => {
        if (currentPageLibro < totalPages) {
            currentPageLibro++;
            renderTableLibros(currentPageLibro);
        }
    });
    pagination.appendChild(nextBtn);
}

//Cargar los libros
function cargarCatLibros() {
    fetch("http://localhost:8080/bibliotecaproyecto/api/libro/getAllPublicosTodos")
            .then(response => response.json())
            .then(response => {
                libros = response;
                librosFiltrados = libros;
                //console.log("Libros cargados desde la API:", libros);
                renderTableLibros(currentPageLibro);
            });
}

//Funcion para mostrar el libro
function mostrarLibro(index) {
    const libro = libros[index];
    //console.log(libro);

    if (libro) {
        let pdfBase64 = libro.archivo;
        //console.log("PDF Base64:", pdfBase64);

        if (pdfBase64) {
            // Verifica si el PDF tiene el prefijo de tipo de datos
            if (pdfBase64.startsWith('data:application/pdf;base64,')) {
                pdfBase64 = pdfBase64.split(',')[1]; // Elimina el prefijo si esta presente
            }

            // Convierte el Base64 a un Blob
            const byteCharacters = atob(pdfBase64);
            const byteNumbers = new Uint8Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i); // Convierte cada caracter a un byte
            }
            const blob = new Blob([byteNumbers], {type: 'application/pdf'});
            const pdfUrl = URL.createObjectURL(blob);

            // Muestra el PDF en el iframe
            const pdfViewer = document.getElementById("pdfViewer");
            pdfViewer.src = pdfUrl;

            // Muestra el modal
            const modal = new bootstrap.Modal(document.getElementById('pdfModal'), {});
            modal.show();
        } else {
            alert("No hay PDF disponible para este libro.");
        }
    } else {
        console.error("No se encontró el libro en el índice:", index);
    }
}

// Buscar con filtrado dinamico
function buscarLibros() {
    const buscarTexto = document.getElementById('buscarLibro').value.toLowerCase();

    librosFiltrados = libros.filter(libro => {
        return (
                libro.nombre.toLowerCase().includes(buscarTexto) ||
                libro.autor.toLowerCase().includes(buscarTexto) ||
                libro.genero.toLowerCase().includes(buscarTexto) ||
                libro.universidad.toLowerCase().includes(buscarTexto)
                );
    });

    currentPageLibro = 1; // reinicia a la primera página
    renderTableLibros(currentPageLibro);
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
