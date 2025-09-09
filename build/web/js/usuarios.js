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

let usuarios = [];
let currentPage = 1;
const rowsPerPage = 10;

//Carga los libros de acuerdo a la paginacion
function renderTable(page = 1) {
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const paginatedUsers = usuarios.slice(start, end);

    let mostrar = "";
    let checkbox = document.getElementById("chkestatus");
    const mostrarEstatus = checkbox.checked ? 0 : 1;

    for (let i = 0; i < paginatedUsers.length; i++) {
        const index = start + i;
        if (paginatedUsers[i].estatus == mostrarEstatus) {
            mostrar += '<tr>';
            mostrar += '<td>' + paginatedUsers[i].nombreUsuario + '</td>';
            mostrar += '<td>' + paginatedUsers[i].rol + '</td>';
            mostrar += '<td>' + paginatedUsers[i].email + '</td>';

            if (mostrarEstatus === 1) {
                mostrar += '<td><button class="btn btn-warning btn-modificarC" data-bs-toggle="modal" data-bs-target="#formularioModal2" onclick="modificarUsuario(' + index + ');"><i class="bi bi-pencil"></i></button></td>';
                mostrar += '<td><button class="btn btn-danger btn-md" onclick="eliminarUsuario(' + index + ');"><i class="bi bi-trash"></i></button></td>';
            } else {
                mostrar += '<td><button class="btn btn-warning btn-modificarC" data-bs-toggle="modal" data-bs-target="#formularioModal2" onclick="modificarUsuario(' + index + ');"><i class="bi bi-pencil"></i></button></td>';
                mostrar += '<td><button class="btn btn-success btn-md" onclick="activarUsuario(' + index + ');"><i class="bi bi-check"></i></button></td>';
            }

            mostrar += '</tr>';
        }
    }

    document.getElementById("tblUsuario").innerHTML = mostrar;
    renderPagination();
}

//Controles de paginación (Anterior, Siguiente, y página actual)
function renderPagination() {
    const totalPages = Math.ceil(usuarios.length / rowsPerPage);
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    if (totalPages <= 1)
        return;

    // Boton Anterior
    const prevBtn = document.createElement("button");
    prevBtn.classList.add("btn", "btn-secondary", "mx-1");
    prevBtn.textContent = "Anterior";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            renderTable(currentPage);
        }
    });
    pagination.appendChild(prevBtn);

    // Indicador de pag
    const pageIndicator = document.createElement("span");
    pageIndicator.classList.add("mx-2", "fw-bold");
    pageIndicator.textContent = `Página ${currentPage} de ${totalPages}`;
    pagination.appendChild(pageIndicator);

    // Boton Siguiente
    const nextBtn = document.createElement("button");
    nextBtn.classList.add("btn", "btn-secondary", "mx-1");
    nextBtn.textContent = "Siguiente";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            renderTable(currentPage);
        }
    });
    pagination.appendChild(nextBtn);
}

//Cargar los usuarios
function cargarCatUsuarios() {
    fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/getAll")
            .then(response => response.json())
            .then(response => {
                usuarios = response;
                //console.log("Usuarios cargados desde la API:", usuarios);
                renderTable(currentPage);
            });
}

//Carga los datos del modal cuando  se modifica
function modificarUsuario(i) {
    //console.log("Índice seleccionado para modificar: ", i);
    const usuario = usuarios[i];

    if (usuario) {
        //console.log("Datos del usuario seleccionado: ", usuario);

        document.getElementById("id").value = usuario.idUsuario || '';
        document.getElementById("no").value = usuario.nombreUsuario || '';
        document.getElementById("co").value = usuario.contrasenia || '';
        document.getElementById("es").value = usuario.estatus;
        document.getElementById("ro").value = usuario.rol;
        document.getElementById("em").value = usuario.email || '';

        console.log("Campos del modal actualizados con éxito.");
    } else {
        console.error("Error: No se encontró el usuario en la posición ", i);
    }
}

//funcion para desactivar un usuario
function eliminarUsuario(i) {
    if (i < 0 || i >= usuarios.length) {
        console.error("Índice fuera de rango:", i);
        return;
    }

    let idUsuarios = usuarios[i].idUsuario;
    //console.log("ID de usuario a eliminar:", idUsuarios);

    fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/delete?idUsuarios=" + idUsuarios)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error en la respuesta del servidor");
                }
                return response.json();
            })
            .then(response => {
                Swal.fire(response.result, "Usuario desactivado correctamente", "success")
                selecionarUsuario();
            })
            .catch(error => {
                console.error("Error al eliminar el usuario:", error);
                Swal.fire("Error", "No se pudo eliminar el usuario. Inténtalo de nuevo.", "error");
            });
}

//funcion para activar
function activarUsuario(i) {
    if (i < 0 || i >= usuarios.length) {
        console.error("Índice fuera de rango:", i);
        return;
    }
    let idUsuarios = usuarios[i].idUsuario;
    //console.log("ID de usuario a activar:", idUsuarios);
    fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/activar?idUsuario=" + idUsuarios)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error en la respuesta del servidor");
                }
                return response.json();
            })
            .then(response => {
                Swal.fire(response.result, "Usuario activado correctamente", "success");
                selecionarUsuario();
            })
            .catch(error => {
                console.error("Error al activar el usuario:", error);
                Swal.fire("Error", "No se pudo activar el usuario. Inténtalo de nuevo.", "error");
            });
}

//funcion para insertar
function insertarUsuario() {
    let nombre = document.getElementById("nombre").value;
    let contrasenia = document.getElementById("contrasenia").value;
    let estatusSelect = document.getElementById("estatus");
    let rolSelect = document.getElementById("rol");
    let email = document.getElementById("email").value;

    let estatus = estatusSelect.value;
    let rol = rolSelect.value;

    // Validaciones antes de enviar
    if (!nombre.trim() || !contrasenia.trim() || !email.trim()) {
        Swal.fire("Error", "Todos los campos son obligatorios", "warning");
        return;
    }

    if (estatus === "") {
        Swal.fire("Error", "Debes seleccionar un estatus", "warning");
        return;
    }

    if (rol === "") {
        Swal.fire("Error", "Debes seleccionar un rol", "warning");
        return;
    }

    const usuario = {
        nombreUsuario: nombre,
        contrasenia: contrasenia,
        estatus: parseInt(estatus),
        rol: rol,
        email: email
    };

    let params = {u: JSON.stringify(usuario)};

    fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/insert", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
        body: new URLSearchParams(params)
    })
            .then(response => {
                if (!response.ok)
                    throw new Error("No se pudo completar la solicitud.");
                return response.json();
            })
            .then(response => {
                if (response.result) {
                    Swal.fire("Correcto", response.result, "success");
                    recargarTabla();
                    cerrarModalInsert();
                } else if (response.error) {
                    Swal.fire("Problema", response.error, "error");
                }
            })
            .catch(error => {
                Swal.fire("Error", error.message, "error");
            });
    limpiarCampos()
}

//funcion para actualizar
function moddUsuario() {

    const idUsuario = document.getElementById("id").value;
    const nombreUsuario = document.getElementById("no").value;
    const contrasenia = document.getElementById("co").value;
    const estatus = document.getElementById("es").value;
    const rol = document.getElementById("ro").value;
    const email = document.getElementById("em").value;

    const usuario = {
        idUsuario: parseInt(idUsuario),
        nombreUsuario: nombreUsuario,
        contrasenia: contrasenia,
        estatus: parseInt(estatus),
        rol: rol,
        email: email
    };

    fetch('http://localhost:8080/bibliotecaproyecto/api/usuario/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `u=${encodeURIComponent(JSON.stringify(usuario))}`
    })
            .then(response => {

                if (!response.ok) {
                    throw new Error("No se pudo completar la solicitud.");
                }
                return response.json();
            })
            .then(response => {
                if (response.result) {
                    Swal.fire("Actualización de usuario correcta", response.result, "success");
                    recargarTabla();
                    cerrarModalupdate();
                } else if (response.error) {
                    Swal.fire("Problemas para actualizar el usuario", response.error, "error");
                }
            })
            .catch(error => {
                console.error("Error en la actualización del usuario:", error);
                Swal.fire("Problemas para actualización el usuario", error.message, "error");
            });
}

//funcion para activos/inactivos
function selecionarUsuario() {
    let checkbox = document.getElementById("chkestatus");

    if (checkbox.checked) {
        // nactivos
        fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/seleccionar?val=0")
                .then(response => response.json())
                .then(response => {
                    usuarios = response;
                    currentPage = 1;
                    renderTable(currentPage);
                })
                .catch(error => console.error("Error al cargar usuarios inactivos:", error));
    } else {
        // activos
        fetch("http://localhost:8080/bibliotecaproyecto/api/usuario/seleccionar?val=1")
                .then(response => response.json())
                .then(response => {
                    usuarios = response;
                    currentPage = 1;
                    renderTable(currentPage);
                })
                .catch(error => console.error("Error al cargar usuarios activos:", error));
    }
}

//funcion para buscar
function buscarUsuario() {
    let busqueda = document.getElementById("campoBusqueda").value;
    if (busqueda === "")
        return;

    let checkbox = document.getElementById("chkestatus");
    let estatusFiltro = checkbox.checked ? 0 : 1;

    fetch(`http://localhost:8080/bibliotecaproyecto/api/usuario/buscar?valor=${busqueda}`)
            .then(response => response.json())
            .then(response => {
                // Pr estatus
                usuarios = response.filter(u => u.estatus === estatusFiltro);

                currentPage = 1;

                renderTable(currentPage);
            })
            .catch(error => console.error("Error al buscar usuarios:", error));
}

//funcion para limpiar los campos
function limpiarCampos() {
    var nombre = document.getElementById('nombre');
    var contrasenia = document.getElementById('contrasenia');
    var estatus = document.getElementById('estatus');
    var rol = document.getElementById('rol');
    var email = document.getElementById('email');

    nombre.value = '';
    contrasenia.value = '';
    estatus.value = '';
    rol.value = '';
    email.value = '';
}

//funcion para recargar la tabla
function recargarTabla() {
    cargarCatUsuarios();
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
