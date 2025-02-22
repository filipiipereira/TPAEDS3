public class Main {
    public static void main(String[] args) {
        LoadCsv lc = new LoadCsv();
        lc.LoadFromCsv();
        SequentialFile sf = new SequentialFile();
        sf.get(1).toStr();
    }
}