import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //long start = System.currentTimeMillis();

        LoadCsv lc = new LoadCsv();
        lc.LoadFromCsv();
        SequentialFile sf = new SequentialFile();

        //get
        sf.get(1).toStr();

        //update
    /*  Film updatedFilm = new Film(1, "Updated Movie", 20221212L, 2000000, 1500000.0f, 
        Arrays.asList("Company A", "Company C"), "Drama");
            if(sf.update(updatedFilm)) System.out.println("OK (Update)");
            else System.out.println("ERRO (Update)");   */

        //delete
    /*  if(sf.delete(0)) System.out.println("OK (Delete)");
        else System.out.println("ERRO (Delete)");   */

        //long end = System.currentTimeMillis();
        //System.out.println(end - start + " milissegundos");
    }
}
