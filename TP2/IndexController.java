
import java.util.ArrayList;

public class IndexController{
    private static final String BTREE_NAME = "arvore.dat";
    private static final String HASH_NAME = "hash.dat";
    private static final String INVERTEDLIST_NAME = "lista.dat";
    private static int index; //implementar
    public static long getPos(int id){
        long pos = 0;
        if(index == 1) pos = BtreeGet(id);
        return pos;
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
    public static void Update(long newPos, int id){
        if(index == 1)BtreeUpdate(newPos, id);
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
    
}