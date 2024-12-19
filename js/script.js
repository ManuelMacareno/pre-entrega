document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM completamente cargado');

    const productosContainer = document.getElementById('productos-container');
    const modal = document.getElementById('descripcion-modal');
    const cerrarModal = document.querySelector('.cerrar');
    const nombreProducto = document.getElementById('nombre-producto');
    const descripcionProducto = document.getElementById('descripcion-producto');

    if (window.location.pathname.includes('productos.html')) {
        cargarProductos(true, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto);
    }

    if (window.location.pathname.includes('index.html')) {
        cargarProductos(false, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto);
    }

    if (window.location.pathname.includes('carrito.html')) {
        cargarCarrito();
    }
});

async function cargarProductos(cargarTodos, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto) {
    if (!productosContainer) {
        console.error('El contenedor de productos no se encontró.');
        return;
    }

    const productosCache = localStorage.getItem('productos');
    if (productosCache) {
        console.log('Cargando productos desde caché');
        mostrarProductosDesdeCache(JSON.parse(productosCache), cargarTodos, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto);
        return;
    }

    try {
        const response = await fetch('https://fakestoreapi.com/products');
        if (!response.ok) {
            throw new Error(`Error en la respuesta de la API: ${response.status}`);
        }
        const productos = await response.json();
        console.log('Datos de productos:', productos);

        if (!Array.isArray(productos)) {
            throw new Error('La respuesta de la API no es un array');
        }

        localStorage.setItem('productos', JSON.stringify(productos));
        mostrarProductosDesdeCache(productos, cargarTodos, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto);
    } catch (error) {
        console.error('Error al cargar los productos:', error);
        productosContainer.innerHTML = `<p>Hubo un problema al cargar los productos. Inténtalo de nuevo más tarde.</p>`;
    }
}

function mostrarProductosDesdeCache(productos, cargarTodos, productosContainer, modal, cerrarModal, nombreProducto, descripcionProducto) {
    const productosMostrar = cargarTodos ? productos : productos.slice(12, 15);

    let productosHTML = '';
    productosMostrar.forEach(producto => {
        const descripcionCorta = producto.description.length > 100 ? producto.description.substring(0, 100) + '...' : producto.description;

        productosHTML += `
            <article class="producto-item" data-id="${producto.id}">
                <h3>${producto.title}</h3>
                <figure>
                    <img src="${producto.image}" alt="Imagen de ${producto.title}">
                    <figcaption>${descripcionCorta}</figcaption>
                </figure>
                <p>Precio: $${producto.price}</p>
                <button aria-label="Añadir ${producto.title} al carrito" class="btn btn-primary">Añadir al carrito</button>
            </article>
        `;
    });

    productosContainer.innerHTML = productosHTML;

    const productoElements = document.querySelectorAll('.producto-item');

    productoElements.forEach(producto => {
        producto.addEventListener('click', function () {
            modal.style.display = 'flex';
            const productoId = producto.getAttribute('data-id');
            const productoCompleto = productos.find(p => p.id == productoId);
            nombreProducto.textContent = productoCompleto.title;
            descripcionProducto.textContent = productoCompleto.description;
        });

        const agregarAlCarritoBtn = producto.querySelector('button');
        agregarAlCarritoBtn.addEventListener('click', function (event) {
            event.stopPropagation();
            const productoId = producto.getAttribute('data-id');
            const productoCompleto = productos.find(p => p.id == productoId);

            let carrito = JSON.parse(localStorage.getItem('carrito')) || [];
            const productoEnCarrito = carrito.find(p => p.id == productoId);

            if (productoEnCarrito) {
                productoEnCarrito.cantidad += 1;
            } else {
                productoCompleto.cantidad = 1;
                carrito.push(productoCompleto);
            }

            localStorage.setItem('carrito', JSON.stringify(carrito));
            mostrarAlerta('Producto añadido al carrito!');
        });
    });

    cerrarModal.addEventListener('click', function () {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

function mostrarAlerta(mensaje) {
    const alerta = document.createElement('div');
    alerta.classList.add('alerta');
    alerta.textContent = mensaje;

    document.body.appendChild(alerta);

    setTimeout(() => {
        alerta.remove();
    }, 5000);
}

function cargarCarrito() {
    const carrito = JSON.parse(localStorage.getItem('carrito')) || [];
    const carritoContainer = document.getElementById('carrito-container');
    const totalContainer = document.getElementById('total');

    if (carrito.length === 0) {
        carritoContainer.innerHTML = '<p>No hay productos en el carrito.</p>';
        totalContainer.textContent = '0.00';
        return;
    }

    let carritoHTML = '';
    let total = 0;

    carrito.forEach((producto, index) => {
        carritoHTML += `
            <div class="producto-carrito">
                <div class="producto-info">
                    <img src="${producto.image}" alt="Imagen de ${producto.title}">
                    <div class="producto-detalles">
                        <h4>${producto.title}</h4>
                    </div>
                    <div class="cantidad-control">
                        <button class="restar-cantidad" data-index="${index}" ${producto.cantidad <= 1 ? 'disabled' : ''}>-</button>
                        <span class="cantidad">${producto.cantidad}</span>
                        <button class="sumar-cantidad" data-index="${index}">+</button>
                    </div>
                </div>
                <span class="producto-precio">$${(producto.price * producto.cantidad).toFixed(2)}</span>
                <button class="eliminar-producto" data-index="${index}">X</button>
            </div>
        `;
        total += producto.price * producto.cantidad;
    });

    carritoContainer.innerHTML = carritoHTML;
    totalContainer.textContent = total.toFixed(2);

    document.querySelectorAll('.sumar-cantidad').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.dataset.index;
            actualizarCantidadCarrito(index, 'sumar');
        });
    });

    document.querySelectorAll('.restar-cantidad').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.dataset.index;
            actualizarCantidadCarrito(index, 'restar');
        });
    });

    document.querySelectorAll('.eliminar-producto').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.dataset.index;
            let carrito = JSON.parse(localStorage.getItem('carrito'));
            carrito.splice(index, 1);
            localStorage.setItem('carrito', JSON.stringify(carrito));
            cargarCarrito(); 
        });
    });
}

function actualizarCantidadCarrito(index, operacion) {
    let carrito = JSON.parse(localStorage.getItem('carrito'));
    if (carrito[index]) {
        if (operacion === 'sumar') {
            carrito[index].cantidad += 1;
        } else if (operacion === 'restar' && carrito[index].cantidad > 1) {
            carrito[index].cantidad -= 1;
        }
        localStorage.setItem('carrito', JSON.stringify(carrito));
        cargarCarrito();
    }
}
