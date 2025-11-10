

public class Bplus {

    private Node root;
    private final int order;

    public Bplus(int order) {
        this.order = order;
        root = new Node(true);
    }
    
    private int getMaxKeys() {
        return order - 1;
    }
    
    private int getMinKeys() {
        return (order + 1) / 2 - 1;
    }

    public void insertar(int clave, String valor) {
        if(root == null) {
            root = new Node(true);
            root.insertarClave(clave, valor);
            return;
        }
        
        insertarRecursivo(root, clave, valor);
        
        // si la raiz se llena hay que dividirla
        if(root.getKeys().size() > getMaxKeys()) {
            Node nuevaRaiz = new Node(false);
            nuevaRaiz.getSons().add(root);
            Node.ResultadoDivision resultado = root.dividir(order);
            nuevaRaiz.getKeys().add(resultado.clavePromocionada);
            nuevaRaiz.getSons().add(resultado.nuevoNodo);
            root = nuevaRaiz;
        }
    }

    private void insertarRecursivo(Node nodo, int clave, String valor) {
        if(nodo.isLeaf()) {
            nodo.insertarClave(clave, valor);
        } else {
            // buscar el hijo correcto
            int i = 0;
            while(i < nodo.getKeys().size() && clave >= nodo.getKeys().get(i)) {
                i++;
            }
            Node hijo = nodo.getSons().get(i);
            
            insertarRecursivo(hijo, clave, valor);
            
            // si el hijo se lleno hay que dividirlo
            if(hijo.getKeys().size() > getMaxKeys()) {
                Node.ResultadoDivision resultado = hijo.dividir(order);
                nodo.getKeys().add(i, resultado.clavePromocionada);
                nodo.getSons().add(i + 1, resultado.nuevoNodo);
            }
        }
    }

    public String buscar(int clave) {
        return buscarEnArbol(root, clave);
    }

    private String buscarEnArbol(Node nodo, int clave) {
        if(nodo == null) {
            return null;
        }
        
        int i = 0;
        while(i < nodo.getKeys().size() && nodo.getKeys().get(i) < clave) {
            i++;
        }
        
        if(i == nodo.getKeys().size()) {
            if(nodo.isLeaf()) {
                return null;
            } else {
                return buscarEnArbol(nodo.getSons().get(nodo.getSons().size() - 1), clave);
            }
        } else if(nodo.getKeys().get(i) > clave) {
            if(nodo.isLeaf()) {
                return null;
            } else {
                return buscarEnArbol(nodo.getSons().get(i), clave);
            }
        } else {
            if(nodo.isLeaf()) {
                return nodo.getValores().get(i);
            } else {
                return buscarEnArbol(nodo.getSons().get(i + 1), clave);
            }
        }
    }

    public boolean eliminar(int clave) {
        if(root == null || root.getKeys().isEmpty()) {
            return false;
        }
        
        boolean resultado = eliminarRecursivo(root, null, -1, clave);
        
        if(!root.isLeaf() && root.getKeys().isEmpty()) {
            if(!root.getSons().isEmpty()) {
                root = root.getSons().get(0);
            } else {
                root = new Node(true);
            }
        }
        
        return resultado;
    }

    private boolean eliminarRecursivo(Node nodo, Node padre, int indiceEnPadre, int clave) {
        if(nodo == null) {
            return false;
        }
        
        int i = 0;
        while(i < nodo.getKeys().size() && nodo.getKeys().get(i) < clave) {
            i++;
        }
        
        if(i == nodo.getKeys().size()) {
            if(nodo.isLeaf()) {
                return false;
            } else {
                int indiceHijo = nodo.getSons().size() - 1;
                boolean resultado = eliminarRecursivo(nodo.getSons().get(indiceHijo), nodo, indiceHijo, clave);
                repararDespuesEliminar(nodo.getSons().get(indiceHijo), nodo, indiceHijo);
                return resultado;
            }
        } else if(!nodo.isLeaf() && nodo.getKeys().get(i) == clave) {
            Node hijoDer = nodo.getSons().get(i + 1);
            boolean resultado = eliminarRecursivo(hijoDer, nodo, i + 1, clave);
            repararDespuesEliminar(hijoDer, nodo, i + 1);
            
            // actualizar la clave en el padre si el primer elemento de la hoja cambio
            if(i < nodo.getSons().size()) {
                Node hijoActualizado = nodo.getSons().get(i + 1);
                if(hijoActualizado.isLeaf() && !hijoActualizado.getKeys().isEmpty()) {
                    int nuevaClave = hijoActualizado.getKeys().get(0);
                    if(nuevaClave != clave) {
                        nodo.getKeys().set(i, nuevaClave);
                    }
                }
            }
            
            return resultado;
        } else if(!nodo.isLeaf()) {
            boolean resultado = eliminarRecursivo(nodo.getSons().get(i), nodo, i, clave);
            repararDespuesEliminar(nodo.getSons().get(i), nodo, i);
            return resultado;
        } else if(nodo.isLeaf() && nodo.getKeys().get(i) == clave) {
            nodo.getKeys().remove(i);
            nodo.getValores().remove(i);
            
            if(i == 0 && nodo.getKeys().size() > 0 && padre != null) {
                actualizarClavesPadres(nodo, padre, indiceEnPadre, nodo.getKeys().get(0));
            }
            
            repararDespuesEliminar(nodo, padre, indiceEnPadre);
            return true;
        } else {
            return false;
        }
    }

    private void actualizarClavesPadres(Node hoja, Node padre, int indiceEnPadre, int nuevaClave) {
        if(padre == null || indiceEnPadre == 0) {
            return;
        }
        
        if(indiceEnPadre > 0 && indiceEnPadre <= padre.getKeys().size()) {
            int indiceClave = indiceEnPadre - 1;
            if(indiceClave >= 0 && indiceClave < padre.getKeys().size()) {
                padre.getKeys().set(indiceClave, nuevaClave);
            }
        }
    }

    private void repararDespuesEliminar(Node nodo, Node padre, int indiceEnPadre) {
        if(nodo == root) {
            if(nodo.getKeys().isEmpty() && !nodo.isLeaf()) {
                if(!nodo.getSons().isEmpty()) {
                    root = nodo.getSons().get(0);
                }
            }
            return;
        }
        
        // si tiene pocas claves hay que rebalancear
        if(nodo.getKeys().size() < getMinKeys() && padre != null) {
            // primero intentar robar del izquierdo
            if(indiceEnPadre > 0) {
                Node hermanoIzq = padre.getSons().get(indiceEnPadre - 1);
                if(hermanoIzq.getKeys().size() > getMinKeys()) {
                    robarDelIzquierdo(nodo, padre, indiceEnPadre);
                    return;
                }
            }
            
            // si no, intentar robar del derecho
            if(indiceEnPadre < padre.getSons().size() - 1) {
                Node hermanoDer = padre.getSons().get(indiceEnPadre + 1);
                if(hermanoDer.getKeys().size() > getMinKeys()) {
                    robarDelDerecho(nodo, padre, indiceEnPadre);
                    return;
                }
            }
            
            // si no se puede robar, hacer merge
            if(indiceEnPadre == 0 && indiceEnPadre + 1 < padre.getSons().size()) {
                // fusionar con el hermano derecho
                fusionarDerecha(nodo, padre, indiceEnPadre);
            } else if(indiceEnPadre > 0) {
                // fusionar con el hermano izquierdo (el nodo actual se fusiona en el izquierdo)
                Node hermanoIzq = padre.getSons().get(indiceEnPadre - 1);
                if(indiceEnPadre < padre.getSons().size()) {
                    fusionarDerecha(hermanoIzq, padre, indiceEnPadre - 1);
                }
            }
            
            // reparar el padre si es necesario
            // despues del merge, el padre tiene una clave menos
            if(padre.getKeys().size() < getMinKeys() && padre != root) {
                buscarYRepararPadre(padre);
            } else if(padre == root && padre.getKeys().isEmpty() && !padre.isLeaf()) {
                if(!padre.getSons().isEmpty()) {
                    root = padre.getSons().get(0);
                }
            }
        }
    }

    private void buscarYRepararPadre(Node nodo) {
        if(nodo == root) {
            return;
        }
        
        Node padreDelPadre = buscarPadreRecursivo(root, null, -1, nodo);
        if(padreDelPadre != null) {
            int indiceEnAbuelo = encontrarIndiceEnPadre(padreDelPadre, nodo);
            if(indiceEnAbuelo >= 0) {
                Node abuelo = buscarPadreRecursivo(root, null, -1, padreDelPadre);
                int indiceAbuelo = abuelo != null ? encontrarIndiceEnPadre(abuelo, padreDelPadre) : -1;
                repararDespuesEliminar(nodo, padreDelPadre, indiceAbuelo);
            }
        }
    }

    private Node buscarPadreRecursivo(Node actual, Node padreActual, int indiceActual, Node objetivo) {
        if(actual == objetivo) {
            return padreActual;
        }
        
        if(actual.isLeaf()) {
            return null;
        }
        
        for(int i = 0; i < actual.getSons().size(); i++) {
            Node resultado = buscarPadreRecursivo(actual.getSons().get(i), actual, i, objetivo);
            if(resultado != null) {
                return resultado;
            }
        }
        
        return null;
    }

    private int encontrarIndiceEnPadre(Node padre, Node hijo) {
        if(padre == null) {
            return -1;
        }
        
        for(int i = 0; i < padre.getSons().size(); i++) {
            if(padre.getSons().get(i) == hijo) {
                return i;
            }
        }
        
        return -1;
    }

    private void robarDelIzquierdo(Node nodo, Node padre, int indiceEnPadre) {
        Node hermanoIzq = padre.getSons().get(indiceEnPadre - 1);
        
        if(nodo.isLeaf()) {
            // mover la ultima clave del hermano izquierdo
            int ultimaClave = hermanoIzq.getKeys().remove(hermanoIzq.getKeys().size() - 1);
            String ultimoValor = hermanoIzq.getValores().remove(hermanoIzq.getValores().size() - 1);
            
            nodo.getKeys().add(0, ultimaClave);
            nodo.getValores().add(0, ultimoValor);
            
            if(indiceEnPadre > 0) {
                padre.getKeys().set(indiceEnPadre - 1, ultimaClave);
            }
        } else {
            // para internos es mas complicado
            int ultimaClave = hermanoIzq.getKeys().remove(hermanoIzq.getKeys().size() - 1);
            Node ultimoHijo = hermanoIzq.getSons().remove(hermanoIzq.getSons().size() - 1);
            
            int claveDelPadre = padre.getKeys().get(indiceEnPadre - 1);
            nodo.getKeys().add(0, claveDelPadre);
            nodo.getSons().add(0, ultimoHijo);
            
            padre.getKeys().set(indiceEnPadre - 1, ultimaClave);
        }
    }

    private void robarDelDerecho(Node nodo, Node padre, int indiceEnPadre) {
        Node hermanoDer = padre.getSons().get(indiceEnPadre + 1);
        
        if(nodo.isLeaf()) {
            int primeraClave = hermanoDer.getKeys().remove(0);
            String primerValor = hermanoDer.getValores().remove(0);
            
            nodo.getKeys().add(primeraClave);
            nodo.getValores().add(primerValor);
            
            if(indiceEnPadre < padre.getKeys().size()) {
                padre.getKeys().set(indiceEnPadre, hermanoDer.getKeys().get(0));
            }
        } else {
            int primeraClave = hermanoDer.getKeys().remove(0);
            Node primerHijo = hermanoDer.getSons().remove(0);
            
            int claveDelPadre = padre.getKeys().get(indiceEnPadre);
            nodo.getKeys().add(claveDelPadre);
            nodo.getSons().add(primerHijo);
            
            padre.getKeys().set(indiceEnPadre, primeraClave);
        }
    }

    private void fusionarDerecha(Node nodoIzq, Node padre, int indiceEnPadre) {
        // verificar que existe el hermano derecho
        if(indiceEnPadre + 1 >= padre.getSons().size()) {
            return;
        }
        
        Node nodoDer = padre.getSons().get(indiceEnPadre + 1);
        
        if(nodoIzq.isLeaf()) {
            nodoIzq.getKeys().addAll(nodoDer.getKeys());
            nodoIzq.getValores().addAll(nodoDer.getValores());
            nodoIzq.setSiguiente(nodoDer.getSiguiente());
        } else {
            if(indiceEnPadre < padre.getKeys().size()) {
                int claveDelPadre = padre.getKeys().get(indiceEnPadre);
                nodoIzq.getKeys().add(claveDelPadre);
            }
            nodoIzq.getKeys().addAll(nodoDer.getKeys());
            nodoIzq.getSons().addAll(nodoDer.getSons());
        }
        
        if(indiceEnPadre < padre.getKeys().size()) {
            padre.getKeys().remove(indiceEnPadre);
        }
        if(indiceEnPadre + 1 < padre.getSons().size()) {
            padre.getSons().remove(indiceEnPadre + 1);
        }
    }

    public void recorrer(int clave, int n) {
        Node hoja = buscarHoja(root, clave);
        
        if(hoja == null) {
            System.out.println("Clave no encontrada.");
            return;
        }
        
        // encontrar donde empieza la clave
        int indice = 0;
        while(indice < hoja.getKeys().size() && hoja.getKeys().get(indice) < clave) {
            indice++;
        }
        
        // recorrer usando los enlaces entre hojas
        int contador = 0;
        Node hojaActual = hoja;
        int indiceActual = indice;
        
        while(contador < n && hojaActual != null) {
            while(indiceActual < hojaActual.getKeys().size() && contador < n) {
                System.out.println("Clave: " + hojaActual.getKeys().get(indiceActual) + 
                                 ", Valor: " + hojaActual.getValores().get(indiceActual));
                contador++;
                indiceActual++;
            }
            if(contador < n) {
                hojaActual = hojaActual.getSiguiente();
                indiceActual = 0;
            }
        }
        
        if(contador == 0) {
            System.out.println("No hay más elementos después de la clave especificada.");
        }
    }

    private Node buscarHoja(Node nodo, int clave) {
        if(nodo == null) {
            return null;
        }
        
        if(nodo.isLeaf()) {
            return nodo;
        }
        
        int i = 0;
        while(i < nodo.getKeys().size() && clave >= nodo.getKeys().get(i)) {
            i++;
        }
        
        return buscarHoja(nodo.getSons().get(i), clave);
    }

    public void imprimirArbol() {
        imprimirNodo(root, "", true);
    }

    private void imprimirNodo(Node nodo, String indentacion, boolean esUltimo) {
        if(nodo == null) {
            return;
        }
        
        if(nodo.isLeaf()) {
            System.out.println(indentacion + "+- Hoja Nodo: " + nodo.getKeys() + " Valores: " + nodo.getValores());
        } else {
            System.out.println(indentacion + "+- Interno Nodo: " + nodo.getKeys());
        }
        indentacion += esUltimo ? "   " : "|  ";
        for(int i = 0; i < nodo.getSons().size(); i++) {
            imprimirNodo(nodo.getSons().get(i), indentacion, i == (nodo.getSons().size() - 1));
        }
    }
}
