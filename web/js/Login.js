
//Funcion de ingresar
function Ingresar() {

    let usuario = document.getElementById("usuario").value;
    let contrasenia = document.getElementById("contrasena").value;

    let params = {u: usuario, c: contrasenia};
    let ruta = "/bibliotecaproyecto/api/acceso/login?";

    fetch(ruta,
            {
                method: "POST",
                headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
                body: new URLSearchParams(params)
            })
            .then(response => response.json())
            .then(response => {

                if (response.rol === 'Administrador') {
                    Swal.fire({
                        title: "Bienvenido administrador",
                        text: "Ingreso correcto.",
                        icon: "success",
                        confirmButtonText: "Continuar"
                    }).then((result) => {
                        if (result.isConfirmed) {

                            window.location.href = 'usuarios.html';
                        }
                    });
                } else if (response.rol === 'Alumno') {
                    Swal.fire({
                        title: "Bienvenido alumno",
                        text: "Ingreso correcto.",
                        icon: "success",
                        confirmButtonText: "Continuar"
                    }).then((result) => {
                        if (result.isConfirmed) {

                            window.location.href = 'alumnos.html';
                        }
                    });
                } else if (response.rol === 'Bibliotecario') {
                    Swal.fire({
                        title: "Bienvenido bibliotecario",
                        text: "Ingreso correcto.",
                        icon: "success",
                        confirmButtonText: "Continuar"
                    }).then((result) => {
                        if (result.isConfirmed) {

                            window.location.href = 'libros.html';
                        }
                    });
                } else if (response.rol === 'Error') {
                    Swal.fire({
                        title: "Permiso denegado",
                        text: "Datos ingresados erroneos.",
                        icon: "error",
                        confirmButtonText: "Volver a intentar"
                    }).then((result) => {
                        if (result.isConfirmed) {
                        }
                    });
                }
            });
}


var m1 = false;
var m2 = false;

function validarCampos() {

    const usuario = document.getElementById('usuario').value;
    const contrasena = document.getElementById('contrasena').value;

    if (usuario === "") {
        Swal.fire({
            title: "Advertencia!",
            text: "El campo de usuario el obligatorio.",
            icon: "warning",
            confirmButtonText: "Continuar"
        }).then(() => {
            document.getElementById("usuario").focus();
        });
        return false;
    } else {
        m1 = true;
    }
    if (contrasena === "") {
        Swal.fire({
            title: "Advertencia!",
            text: "El campo de contraseña es obligatorio.",
            icon: "warning",
            confirmButtonText: "Continuar"
        }).then(() => {
            document.getElementById("usuario").focus();
        });
        return false;
    } else {
        m2 = true;
    }

    if (m1 == true && m2 == true) {
        Ingresar();
        m1 = false;
        m2 = false;
    }
}

document.getElementById("iniciarSesion").addEventListener("click", function (event) {
    event.preventDefault();
    validarCampos();
});
