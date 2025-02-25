public class Main {
    public static void main(String[] args) {
        //long start = System.currentTimeMillis();
        LoadCsv lc = new LoadCsv();
        lc.LoadFromCsv();
        SequentialFile sf = new SequentialFile();
        sf.get(1).toStr();
        if(sf.delete(0)) System.out.println("OK (Delete)");
        else System.out.println("ERRO (Delete)");
        //long end = System.currentTimeMillis();
        //System.out.println(end - start + "milissegundos");
    }
}
