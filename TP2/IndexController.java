
import java.util.ArrayList;

public class IndexController{
    private static final String BTREE_NAME = "tree.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";
    private static final String DICIONARYNAME_LIST_NAME = "dicionaryListName.dat";
    private static final String BLOCOSNAME_LIST_NAME = "blocosListName.dat";
    private static final String DICIONARYGENRE_LIST_NAME = "dicionaryGenre.dat";
    private static final String BLOCOSGENRE_LIST_NAME = "blocosListGenre.dat";

    public static void Create(Movie movie, long pos, ArvoreBMais bTree, HashExtensivel he, ListaInvertida listName, ListaInvertida listGenre){
        try {
            he.create(new ParIntLongHash(movie.getId(), pos)); //create hash
            bTree.create(new ParIntLong(movie.getId(), pos)); //create btree
            String[] wordsOfName = movie.getName().split(" ");
            for(String word : wordsOfName) if(word.length() > 3) listName.create(word.toLowerCase().trim(), new ElementoLista(movie.getId(), pos));
            listGenre.create(movie.getGenre().toLowerCase(), new ElementoLista(movie.getId(), pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long GetPos(int id, int index){
        long pos = 0;
        switch(index) {
            case 1:
                pos = BtreeGet(id);
                break;
            case 2:
                pos = ExtendedHashGet(id);
                break;
        }
        return pos;
    }
    public static ElementoLista[] GetPosLista(String palavra, int option){
        ListaInvertida lista;
        ElementoLista[] elementos = null;
        try {
            if(option == 1) lista = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
            else lista = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
            elementos = lista.read(palavra.toLowerCase().trim());
            System.out.println("Tamanho lista de elementos: " + elementos.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementos;
    }
    public static long BtreeGet(int id){ 
        long pos = 0;
        try {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            ArrayList<ParIntLong> lista = bTree.read(new ParIntLong(id, -1));
            pos = lista.get(0).getNum2();
        } catch (Exception e) {
        }
        return pos;
    }

    public static long ExtendedHashGet(int id) { 
        long pos = -1;
        try {
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH,
            BUCKET_HASH);
            ParIntLongHash par = he.read(ParIntLongHash.hash(id));
            pos = par.getPos();
        } catch (Exception e) {
        }
        return pos;
}

    public static void Update(long newPos, int id){
        BtreeUpdate(newPos, id); 
        ExtendedHashUpdate(newPos,id);
        //InvertedList(newPos,id){}
    }

    public static void BtreeUpdate(long newPos, int id){
        try {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            bTree.delete(new ParIntLong(id, -1));
            bTree.create(new ParIntLong(id, newPos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ExtendedHashUpdate(long newPos, int id) {
        try {
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH,
        BUCKET_HASH);
            he.delete(id);
            he.create(new ParIntLongHash(id,newPos));
        } catch (Exception e) {
        }
    }

    public static boolean Delete(int id) {
        return BtreeDelete(id) && ExtendedHashDelete(id);
    }

    public static boolean BtreeDelete(int id){
        boolean deletado = false;
        try {
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH, BUCKET_HASH);
            deletado = he.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletado;
    }

    public static boolean ExtendedHashDelete(int id) {
        boolean deletado = false;
        try {
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH, BUCKET_HASH);
           deletado = he.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletado;
    }

    //public static boolean InvertedListDelete(int id){} IMPLEMENTAR
}