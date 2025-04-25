
import java.util.ArrayList;

public class IndexController{
    private static final String BTREE_NAME = "arvore.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";
    private static final String INVERTEDLIST_NAME = "lista.dat";
    private static int index; 
    public static long getPos(int id, int index){
        long pos = 0;
        switch(index) {
            case 1:
                pos = BtreeGet(id);
                break;
            case 2:
                pos = ExtendedHashGet(id);
                break;
            case 3:
               // pos = InvertedListGet(id);
               break;
            default:
                System.out.println("Index inválido!");
                break;
        }
        return pos;
    }
    public static long BtreeGet(int id){ //nao ta funcionando ainda
        long pos = 0;
        try {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            ArrayList<ParIntLong> lista = bTree.read(new ParIntLong(id, -1));
            pos = lista.get(0).getNum2();
        } catch (Exception e) {
        }
        return pos;
    }

    public static long ExtendedHashGet(int id) { //ok
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

    public static void Update(long newPos, int id, int index){

        switch(index) {
            case 1:
                BtreeUpdate(newPos, id); //nao sei
                break;
            case 2:
                ExtendedHashUpdate(newPos,id); //ok
                break;
            case 3:
                //InvertedList(newPos,id);
                break;
            default:
                System.out.println("Opção Inválida!!");
                break;
        }
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

    public static boolean Delete(int id,int index) {
        boolean deletado = false;
            switch(index) {
            case 1:
               //deletado = BtreeDelete(id); IMPLEMENTAR
                break;
            case 2:
                deletado = ExtendedHashDelete(id);
                break;
            case 3:
               // deletado = InvertedListDelete(id);
               break;
            default:
                System.out.println("Index inválido!");
                break;
        }
        return deletado;
    }

    //public static boolean BtreeDelete(int id ){} IMPLEMENTAR

    public static boolean ExtendedHashDelete(int id) {
        boolean deletado = false;
        try {
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH,
        BUCKET_HASH);
           deletado = he.delete(id);
        } catch (Exception e) {
        }
        return deletado;
    }

    //public static boolean InvertedListDelete(int id){} IMPLEMENTAR
}