
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Bplus arbol = null;
        
        System.out.println("=== Árbol B+ ===");
        System.out.println("Bienvenido al sistema de gestión de árbol B+");
        
        System.out.print("\nIngrese el orden del árbol (mínimo 3): ");
        int orden = scanner.nextInt();
        scanner.nextLine();
        if(orden < 3) {
            System.out.println("El orden debe ser al menos 3. Se usará orden 3.");
            orden = 3;
        }
        arbol = new Bplus(orden);
        System.out.println("Árbol B+ creado con orden " + orden + ".");
        
        boolean continuar = true;
        while(continuar) {
            limpiarConsola();
            menu();
            System.out.print("\nSeleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();
            
            limpiarConsola();
            
            switch(opcion) {
                case 1:
                    System.out.print("Ingrese la clave (número entero): ");
                    int clave = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Ingrese el valor (texto): ");
                    String valor = scanner.nextLine();
                    arbol.insertar(clave, valor);
                    System.out.println("\nElemento insertado correctamente.");
                    break;
                    
                case 2:
                    System.out.print("Ingrese la clave a buscar: ");
                    int claveBuscar = scanner.nextInt();
                    scanner.nextLine();
                    String resultado = arbol.buscar(claveBuscar);
                    if(resultado != null) {
                        System.out.println("\nClave encontrada. Valor: " + resultado);
                    } else {
                        System.out.println("\nClave no encontrada.");
                    }
                    break;
                    
                case 3:
                    System.out.print("Ingrese la clave a eliminar: ");
                    int claveEliminar = scanner.nextInt();
                    scanner.nextLine();
                    boolean eliminado = arbol.eliminar(claveEliminar);
                    if(eliminado) {
                        System.out.println("\nClave eliminada correctamente.");
                    } else {
                        System.out.println("\nClave no encontrada para eliminar.");
                    }
                    break;
                    
                case 4:
                    System.out.print("Ingrese la clave de inicio: ");
                    int claveInicio = scanner.nextInt();
                    System.out.print("Ingrese el número de elementos a mostrar: ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("\nRecorrido desde la clave " + claveInicio + ":");
                    arbol.recorrer(claveInicio, n);
                    break;
                    
                case 5:
                    System.out.println("Estructura del árbol:\n");
                    arbol.imprimirArbol();
                    break;
                    
                case 6:
                    continuar = false;
                    System.out.println("¡Hasta luego!");
                    break;
                    
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción del 1 al 6.");
                    scanner.nextLine();
                    break;
            }
            
            if(continuar) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void limpiarConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    private static void menu() {
        System.out.println("========================================");
        System.out.println("           MENÚ PRINCIPAL");
        System.out.println("========================================");
        System.out.println("1. Insertar elemento (clave y valor)");
        System.out.println("2. Buscar elemento por clave");
        System.out.println("3. Eliminar elemento por clave");
        System.out.println("4. Recorrer elementos desde una clave");
        System.out.println("5. Imprimir estructura del árbol");
        System.out.println("6. Salir");
        System.out.println("========================================");
    }
}
