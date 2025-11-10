
import java.util.ArrayList;
import java.util.List;

public class Node {

    private boolean isLeaf;
    private List<Integer> keys;
    private List<Node> sons;
    private List<String> valores;
    private Node siguiente;

    public Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.sons = new ArrayList<>();
        if (isLeaf) {
            this.valores = new ArrayList<>();
            this.siguiente = null;
        } else {
            this.valores = null;
            this.siguiente = null;
        }
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public List<Node> getSons() {
        return sons;
    }

    public List<String> getValores() {
        return valores;
    }

    public Node getSiguiente() {
        return siguiente;
    }

    public void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    public void setSons(List<Node> sons) {
        this.sons = sons;
    }

    public void setValores(List<String> valores) {
        this.valores = valores;
    }

    public void setSiguiente(Node siguiente) {
        this.siguiente = siguiente;
    }

    public void insertarClave(int clave, String valor) {
        // buscar posicion correcta
        int i = 0;
        while (i < keys.size() && clave > keys.get(i)) {
            i++;
        }
        keys.add(i, clave);
        if (isLeaf) {
            valores.add(i, valor);
        }
    }

    public void insertarClave(int clave) {
        if (!isLeaf) {
            int i = 0;
            while (i < keys.size() && clave > keys.get(i)) {
                i++;
            }
            keys.add(i, clave);
        }
    }

    public static class ResultadoDivision {
        public Node nuevoNodo;
        public int clavePromocionada;

        public ResultadoDivision(Node nuevoNodo, int clavePromocionada) {
            this.nuevoNodo = nuevoNodo;
            this.clavePromocionada = clavePromocionada;
        }
    }

    public ResultadoDivision dividir(int orden) {
        int mitad = orden / 2;
        Node nuevoNodo = new Node(this.isLeaf);

        if (isLeaf) {
            // en hojas se mantiene la clave en ambos lados
            nuevoNodo.setKeys(new ArrayList<>(this.keys.subList(mitad, this.keys.size())));
            nuevoNodo.setValores(new ArrayList<>(this.valores.subList(mitad, this.valores.size())));
            
            this.keys = new ArrayList<>(this.keys.subList(0, mitad));
            this.valores = new ArrayList<>(this.valores.subList(0, mitad));

            nuevoNodo.setSiguiente(this.siguiente);
            this.siguiente = nuevoNodo;

            int clavePromocionada = nuevoNodo.getKeys().get(0);
            return new ResultadoDivision(nuevoNodo, clavePromocionada);
        } else {
            // en internos la clave media sube al padre
            int claveMedia = this.keys.get(mitad);
            nuevoNodo.setKeys(new ArrayList<>(this.keys.subList(mitad + 1, this.keys.size())));
            this.keys = new ArrayList<>(this.keys.subList(0, mitad));

            nuevoNodo.setSons(new ArrayList<>(this.sons.subList(mitad + 1, this.sons.size())));
            this.sons = new ArrayList<>(this.sons.subList(0, mitad + 1));

            return new ResultadoDivision(nuevoNodo, claveMedia);
        }
    }
}