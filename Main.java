import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Film {
    private String name;
    private long date;
    private int budget;
    private float boxOffice;
    private String genre;
    private List<String> financingCompanies;

    public Film() {
        this.name = null;
        this.date = -1;
        this.budget = -1;
        this.boxOffice = -1;
        this.financingCompanies = null;
        this.genre = null;
    }

    public Film(String name, long date, int budget, float boxOffice, List<String> financingCompanies, String genre) {
        this.name = name;
        this.date = date;
        this.budget = budget;
        this.boxOffice = boxOffice;
        this.financingCompanies = financingCompanies;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public float getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(float boxOffice) {
        this.boxOffice = boxOffice;
    }

    public List<String> getFinancingCompanies() {
        return financingCompanies;
    }

    public void setFinancingCompanies(List<String> financingCompanies) {
        this.financingCompanies = financingCompanies;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int byteSize(){
        int size = 0;
        size += 2 + name.getBytes().length; // name; writeUTF() uses 2 bytes for the size of the string
        size += 8; // date
        size += 4; //budget 
        size += 4; //boxOffice 
        size += 10; //genre; fixed-size string
        //list of financingCompanies
        size += 4; // size of list
        for (String fc : financingCompanies) {
            size += 2 + fc.getBytes().length; // financingCompanies
        }
        //endlist
        return size;
    }
}

class LoadCsv{
    public void LoadFromCsv(){
        String linha;
        SequentialFile sequentialFile = new SequentialFile();
        try(FileReader fileReader = new FileReader("filmsDataSet.csv")){
            BufferedReader br = new BufferedReader(fileReader);
            linha = br.readLine();//ignores first line
            while(br.readLine() != null){
                String[] values = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String name = values[0];
                LocalDate date = LocalDate.parse(values[1]);
                long epochDate = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                int budget = Integer.parseInt(values[2]);
                float boxOffice = Float.parseFloat(values[3]);
                String genre = values[4];
                List<String> financingCompanies = new ArrayList<>(Arrays.asList(values[5].replace("\"", "").split(",")));
                Film film = new Film(name, epochDate, budget, boxOffice, financingCompanies, genre);
                sequentialFile.Insert(film);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class SequentialFile{
    private String name;
    public SequentialFile(){
        this.name = "SequentialFile.dat";
    }
    public boolean Insert(Film film){
        try (RandomAccessFile file = new RandomAccessFile(name, "w")) {
            file.seek(0);
            int lastId = file.readInt();
            int objectId = lastId + 1;
            file.seek(0);
            file.writeInt(objectId);
            file.seek(file.length());
            file.writeByte(0);//flag
            file.writeInt(film.byteSize());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        
    }
}