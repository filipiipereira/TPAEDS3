import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Representa um filme com atributos como nome, data de lançamento, orçamento,
 * bilheteria global, gênero e empresas financiadoras.
 */

public class Movie {
    private int id;
    private String name;
    private long date;
    private int budget;
    private float boxOffice;
    private String genre;
    private List<String> financingCompanies;

    /**
     * Construtor padrão que inicializa os atributos com valores padrão.
     */

    public Movie() {
        this.id = -1;
        this.name = null;
        this.date = -1;
        this.budget = -1;
        this.boxOffice = -1;
        this.financingCompanies = null;
        this.genre = null;
    }

    /**
     * Construtor que inicializa um filme com valores específicos.
     * 
     * @param id Identificador do filme.
     * @param name Nome do filme.
     * @param date Data de lançamento em formato timestamp.
     * @param budget Orçamento do filme.
     * @param boxOffice Bilheteria global do filme.
     * @param financingCompanies Lista de empresas financiadoras.
     * @param genre Gênero do filme.
     */

    public Movie(int id, String name, long date, int budget, float boxOffice, List<String> financingCompanies, String genre) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.budget = budget;
        this.boxOffice = boxOffice;
        this.financingCompanies = financingCompanies;
        this.genre = genre;
    }

    /**
     * @return O identificador do filme.
     */
    public int getId() {
        return id;
    }

    /**
     * Define o identificador do filme.
     * 
     * @param id Novo identificador do filme.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return O nome do filme.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do filme.
     * 
     * @param name Novo nome do filme.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return A data de lançamento do filme em formato timestamp.
     */
    public long getDate() {
        return date;
    }

    /**
     * Define a data de lançamento do filme.
     * 
     * @param date Nova data de lançamento em formato timestamp.
     */

    public void setDate(long date) {
        this.date = date;
    }

    /**
     * @return O orçamento do filme.
     */

    public int getBudget() {
        return budget;
    }

    /**
     * Define o orçamento do filme.
     * 
     * @param budget Novo orçamento do filme.
     */

    public void setBudget(int budget) {
        this.budget = budget;
    }

    /**
     * @return A bilheteria global do filme.
     */

    public float getBoxOffice() {
        return boxOffice;
    }

    /**
     * Define a bilheteria global do filme.
     * 
     * @param boxOffice Nova bilheteria global do filme.
     */
    
    public void setBoxOffice(float boxOffice) {
        this.boxOffice = boxOffice;
    }

    /**
     * @return A lista de empresas financiadoras.
     */

    public List<String> getFinancingCompanies() {
        return financingCompanies;
    }

    /**
     * Define a lista de empresas financiadoras.
     * 
     * @param financingCompanies Nova lista de empresas financiadoras.
     */

    public void setFinancingCompanies(List<String> financingCompanies) {
        this.financingCompanies = financingCompanies;
    }

    /**
     * @return O gênero do filme.
     */

    public String getGenre() {
        return genre;
    }

    /**
     * Define o gênero do filme.
     * 
     * @param genre Novo gênero do filme.
     */

    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Calcula o tamanho do registro do filme em bytes.
     * 
     * @return O tamanho do registro em bytes.
     */

    public int registerByteSize(){
        int size = 0;
        size += 4; // id
        size += 2 + name.getBytes().length; // name; writeUTF() usa 2 bytes para o tamanho da string
        size += 8; // date
        size += 4; // budget 
        size += 4; // boxOffice 
        size += 10; // genre; string de tamanho fixo
        // Lista de empresas financiadoras
        size += 4; // tamanho da lista
        for (String fc : financingCompanies) {
            size += 2 + fc.getBytes().length; // empresas financiadoras
        }
        return size;
    }

    /**
     * Formata um timestamp para uma string no formato dd/MM/yyyy.
     * 
     * @param timestamp Timestamp a ser formatado.
     * @return Data formatada como string.
     */

    public String dateFormater(long timestamp) {
        LocalDate data = Instant.ofEpochSecond(timestamp+86400).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }

    /**
     * Exibe as informações do filme formatadas no console.
     */

    public void toStr(){
        System.out.print("\nFILM: \nName: \"" + name + "\"; \nRelease Date: " + dateFormater(date) +
            "; \nBudget: " + budget + "$; \nGlobal Box-Office: " + boxOffice + "$; \nGenre: " + genre +
            "; \nFinancing companies: ");
        for(int i = 0; i < financingCompanies.size(); i++){
            if(i == financingCompanies.size() - 1) System.out.println(financingCompanies.get(i));
            else System.out.print(financingCompanies.get(i) + "\", \"");
        }
    }

    int getReleaseDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}