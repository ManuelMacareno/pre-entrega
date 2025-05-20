import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<Articulo> lista = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = 0;
        do {
            System.out.println("\n--- Menu de artículos ---");
            System.out.println("1. Crear artículo");
            System.out.println("2. Listar artículos");
            System.out.println("3. Modificar artículo");
            System.out.println("4. Eliminar artículo");
            System.out.println("5. Salir");

            boolean opcionValida = false;
            while (!opcionValida) {
                System.out.print("Seleccione una opcion (1-5): ");
                if (sc.hasNextInt()) {
                    opcion = sc.nextInt();
                    sc.nextLine();
                    if (opcion >= 1 && opcion <= 5) {
                        opcionValida = true;
                    } else {
                        System.out.println("Opción inválida. Debe ser un número entre 1 y 5.");
                    }
                } else {
                    System.out.println("Entrada inválida. Por favor, ingrese un número.");
                    sc.nextLine();
                }
            }

            switch (opcion) {
                case 1 -> crearArticulo();
                case 2 -> listarArticulo();
                case 3 -> modificarArticulo();
                case 4 -> eliminarArticulo();
                case 5 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida (esto no debería ocurrir).");
            }
        } while (opcion != 5);
        sc.close();
    }

    public static void crearArticulo() {
        System.out.println("\n--- Crear Artículo ---");
        int id = 0;
        boolean idValido = false;
        while (!idValido) {
            System.out.print("ID (entero positivo y único): ");
            if (sc.hasNextInt()) {
                id = sc.nextInt();
                sc.nextLine();
                if (id <= 0) {
                    System.out.println("El ID debe ser un número positivo.");
                    continue;
                }
                boolean existe = false;
                for (Articulo art : lista) {
                    if (art.id == id) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    System.out.println("Error: Ya existe un artículo con ese ID. Intente con otro.");
                } else {
                    idValido = true;
                }
            } else {
                System.out.println("Entrada inválida para ID. Debe ser un número entero.");
                sc.nextLine();
            }
        }

        String nombre = "";
        while (true) {
            System.out.print("Nombre (no puede estar vacío): ");
            nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío. Intente de nuevo.");
            } else {
                break;
            }
        }

        double precio = -1.0;
        boolean precioValido = false;
        while (!precioValido) {
            System.out.print("Precio (numérico, mayor o igual a 0): ");
            if (sc.hasNextDouble()) {
                precio = sc.nextDouble();
                sc.nextLine();
                if (precio < 0) {
                    System.out.println("El precio no puede ser negativo. Intente de nuevo.");
                } else {
                    precioValido = true;
                }
            } else {
                System.out.println("Entrada inválida para precio. Debe ser un número (ej: 10.99).");
                sc.nextLine();
            }
        }

        Articulo nuevo = new Articulo(id, nombre, precio);
        lista.add(nuevo);
        System.out.println("Artículo agregado exitosamente.");
    }

    public static void listarArticulo() {
        System.out.println("\n--- Listado de Artículos ---");
        if (lista.isEmpty()) {
            System.out.println("No hay artículos cargados en el sistema.");
        } else {
            for (Articulo articulo : lista) {
                articulo.mostrar();
            }
        }
    }

    public static void modificarArticulo() {
        System.out.println("\n--- Modificar Artículo ---");
        if (lista.isEmpty()) {
            System.out.println("No hay artículos para modificar.");
            return;
        }

        int idModificar = 0;
        Articulo articuloAModificar = null;
        boolean idEncontradoYValido = false;

        while (!idEncontradoYValido) {
            System.out.print("ID del artículo a modificar (o 0 para cancelar): ");
            if (sc.hasNextInt()) {
                idModificar = sc.nextInt();
                sc.nextLine();

                if (idModificar == 0) {
                    System.out.println("Modificación cancelada.");
                    return;
                }

                boolean encontrado = false;
                for (Articulo art : lista) {
                    if (art.id == idModificar) {
                        articuloAModificar = art;
                        encontrado = true;
                        break;
                    }
                }

                if (encontrado) {
                    idEncontradoYValido = true;
                } else {
                    System.out.println("Artículo con ID " + idModificar + " no encontrado. Intente de nuevo.");
                }
            } else {
                System.out.println("Entrada inválida para ID. Debe ser un número entero.");
                sc.nextLine();
            }
        }

        System.out.println("Modificando artículo:");
        articuloAModificar.mostrar();

        System.out.print("Nuevo Nombre (actual: '" + articuloAModificar.nombre + "', dejar vacío para no cambiar): ");
        String nuevoNombreInput = sc.nextLine().trim();
        if (!nuevoNombreInput.isEmpty()) {
            articuloAModificar.nombre = nuevoNombreInput;
        }

        while (true) {
            System.out.print("Nuevo Precio (actual: $" + articuloAModificar.precio
                    + ", dejar vacío para no cambiar, o ingrese número >= 0): ");
            String precioInputStr = sc.nextLine().trim();

            if (precioInputStr.isEmpty()) {
                break;
            }

            Scanner tempScanner = new Scanner(precioInputStr);
            if (tempScanner.hasNextDouble()) {
                double precioTemp = tempScanner.nextDouble();
                if (tempScanner.hasNext()) {
                    System.out.println("Formato de precio inválido. Ingrese solo un número.");
                } else {
                    if (precioTemp < 0) {
                        System.out.println("El precio no puede ser negativo.");
                    } else {
                        articuloAModificar.precio = precioTemp;
                        break;
                    }
                }
            } else {
                System.out.println("Entrada inválida para precio. Debe ser un número (ej: 10.99).");
            }
            tempScanner.close();
        }
        System.out.println("Artículo modificado exitosamente.");
    }

    public static void eliminarArticulo() {
        System.out.println("\n--- Eliminar Artículo ---");
        if (lista.isEmpty()) {
            System.out.println("No hay artículos para eliminar.");
            return;
        }

        int idEliminar = 0;
        boolean idEncontradoYValido = false;

        while (!idEncontradoYValido) {
            System.out.print("ID del artículo a eliminar (o 0 para cancelar): ");
            if (sc.hasNextInt()) {
                idEliminar = sc.nextInt();
                sc.nextLine();

                if (idEliminar == 0) {
                    System.out.println("Eliminación cancelada.");
                    return;
                }

                boolean existe = false;
                for (Articulo art : lista) {
                    if (art.id == idEliminar) {
                        art.mostrar();
                        existe = true;
                        break;
                    }
                }

                if (existe) {
                    idEncontradoYValido = true;
                } else {
                    System.out.println("Artículo con ID " + idEliminar + " no encontrado. Intente de nuevo.");
                }
            } else {
                System.out.println("Entrada inválida para ID. Debe ser un número entero.");
                sc.nextLine();
            }
        }

        System.out.print("¿Está seguro de que desea eliminar este artículo? (s/N): ");
        String confirmacion = sc.nextLine().trim().toLowerCase();

        if (confirmacion.equals("s")) {
            final int idToDelete = idEliminar;
            boolean removido = lista.removeIf(a -> a.id == idToDelete);
            if (removido) {
                System.out.println("Artículo eliminado exitosamente.");
            } else {
                System.out.println("Error: El artículo no pudo ser eliminado.");
            }
        } else {
            System.out.println("Eliminación cancelada por el usuario.");
        }
    }
}