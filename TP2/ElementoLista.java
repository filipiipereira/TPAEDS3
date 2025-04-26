public class ElementoLista implements Comparable<ElementoLista>, Cloneable {
    
    private int id;
    private long position;

    public ElementoLista(int i, long position) {
        this.id = i;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getposition() {
        return position;
    }

    public void setposition(long position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "("+this.id+")";
    }

    @Override
    public ElementoLista clone() {
        try {
            return (ElementoLista) super.clone();
        } catch (CloneNotSupportedException e) {
            // Tratamento de exceção se a clonagem falhar
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(ElementoLista outro) {
        return Integer.compare(this.id, outro.id);
    }
}