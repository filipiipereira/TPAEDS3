
import java.util.List;

public class Film {
    private int id;
    private String name;
    private long date;
    private int budget;
    private float boxOffice;
    private String genre;
    private List<String> financingCompanies;

    public Film() {
        this.id = -1;
        this.name = null;
        this.date = -1;
        this.budget = -1;
        this.boxOffice = -1;
        this.financingCompanies = null;
        this.genre = null;
    }

    public Film(int id, String name, long date, int budget, float boxOffice, List<String> financingCompanies, String genre) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.budget = budget;
        this.boxOffice = boxOffice;
        this.financingCompanies = financingCompanies;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        size += 4; //id
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

    public void toStr(){
        System.out.print("Nome: " + name + "; Data de lançamento: " + date + "; Orçamento: " + budget + "; Bilheteria global: " + boxOffice + "; Genêro: " + genre + "; Companhias financiadoras: ");
        for(String company : financingCompanies){
            System.out.print(company + ", ");
        }
        System.err.println("");
    }
}