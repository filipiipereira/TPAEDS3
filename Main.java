public class Main {
    public static void main(String[] args) {
        LoadCsv lc = new LoadCsv();
        lc.LoadFromCsv();
        SequentialFile sf = new SequentialFile();
        sf.get(4).toStr();
        if(sf.delete(4)) System.out.println("Deletado!");
        else System.out.println("Erro!");
    }
}