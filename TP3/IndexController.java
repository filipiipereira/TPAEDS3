
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
            for(String word : wordsOfName) if(word.length() > 3){
                String wordSemCaracter = filtraLetras(word);
                if(!wordSemCaracter.equals(""))
                listName.create(wordSemCaracter.toLowerCase().trim(), new ElementoLista(movie.getId(), pos));
            }
             
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

    public static ElementoLista[] GetPosLista(String palavra1,String palavra2, int option){
        ListaInvertida listaNome = null;
        ListaInvertida listaGenre = null;
        ElementoLista[] elementos = null;
        try {
            if(option == 1){
                listaNome = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
                elementos = listaNome.read(palavra1.toLowerCase().trim());
            }
            else if(option == 2){
                listaGenre = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
                elementos = listaGenre.read(palavra1.toLowerCase().trim());
            }
            else{
                listaGenre = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
                listaNome = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
                ElementoLista[] elementosNome = listaNome.read(palavra1.toLowerCase().trim());
                ElementoLista[] elementosGenre = listaGenre.read(palavra2.toLowerCase().trim());
                elementos = Intersection(elementosNome, elementosGenre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementos;
    }
    public static ElementoLista[] Intersection(ElementoLista[] elementosNome, ElementoLista[] elementosGenre){
        ElementoLista[] maior;
        ElementoLista[] menor;
        if(elementosNome.length > elementosGenre.length){
            maior = elementosNome;
            menor = elementosGenre;
        }
        else{
            maior = elementosGenre;
            menor = elementosNome;
        }
        ElementoLista[] elementos = new ElementoLista[menor.length];
        int quantidade = 0;
        int i = 0;
        int j = 0;
        while(i < maior.length && j < menor.length){
            if(maior[i].getId() == menor[j].getId()){
                elementos[quantidade] = maior[i];
                quantidade++;
                i++;
                j++;
            }
            else if(maior[i].getId() < menor[j].getId()) i++;
            else j++;
        }
        ElementoLista[] returnElementos = new ElementoLista[quantidade];
        for(int k = 0; k < quantidade; k++){
            returnElementos[k] = elementos[k];
        }
        return returnElementos;
    }

    public static long BtreeGet(int id){ 
        long pos = 0;
        try {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            ArrayList<ParIntLong> lista = bTree.read(new ParIntLong(id, -1));
            if(!lista.isEmpty()) {
                pos = lista.get(0).getNum2();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    public static void InvertedListUpdate(Movie oldMovie, Movie newMovie, long pos){
        try {
            ListaInvertida listaGenre = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
            ListaInvertida listaNome = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
            String[] wordsOfOldName = oldMovie.getName().split(" ");
            for(String word : wordsOfOldName) if(word.length() > 3){
                String wordSemCaracter = filtraLetras(word);
                if(!wordSemCaracter.equals("")){
                    System.out.println("Nome: " + wordSemCaracter.toLowerCase().trim() + " ID: " + oldMovie.getId());
                    listaNome.delete(wordSemCaracter.toLowerCase().trim(), oldMovie.getId());
                }
                
            }    
            listaGenre.delete(oldMovie.getGenre().toLowerCase(), oldMovie.getId());
            String[] wordsOfName = newMovie.getName().split(" ");
            for(String word : wordsOfName) if(word.length() > 3){
                String wordSemCaracter = filtraLetras(word);
                if(!wordSemCaracter.equals(""))
                listaNome.create(wordSemCaracter.toLowerCase().trim(), new ElementoLista(newMovie.getId(), pos));
            }
            listaGenre.create(newMovie.getGenre().toLowerCase(), new ElementoLista(newMovie.getId(), pos));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean Delete(int id) {
        return BtreeDelete(id) && ExtendedHashDelete(id);
    }

    public static boolean BtreeDelete(int id){
        boolean deletado = false;
        try {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            deletado = bTree.delete(new ParIntLong(id,-1));
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

    public static boolean InvertedListDelete(int id, Movie deletedMovie){
        boolean deletado = false;
        try {  
            ListaInvertida listaGenre = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
            ListaInvertida listaNome = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
            String[] wordsOfOldName = deletedMovie.getName().split(" ");
            for(String word : wordsOfOldName) if(word.length() > 3){
                String wordSemCaracter = filtraLetras(word);
                if(!wordSemCaracter.equals("")){
                    System.out.println("Nome: " + wordSemCaracter.toLowerCase().trim() + " ID: " + id);
                    listaNome.delete(wordSemCaracter.toLowerCase().trim(), id);
                }
            }    
            listaGenre.delete(deletedMovie.getGenre().toLowerCase(), id);
            deletado = true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return deletado;
    }

    public static String filtraLetras(String texto) {
    StringBuilder resultado = new StringBuilder();

    for (int i = 0; i < texto.length(); i++) {
        char c = texto.charAt(i);
        if (Character.isLetter(c)) {
            resultado.append(c);
        }
    }
        return resultado.toString();
    }
}