import java.time.LocalDate;
import java.util.List;

class Filme {
    private String nome;
    private LocalDate data;
    private int investimento;
    private float bilheteira;
    private String genero;
    private List<String> financiadores;

    public Filme() {
        this.nome = null;
        this.data = null;
        this.investimento = 0;
        this.bilheteira = 0;
        this.financiadores = null;
        this.genero = null;
    }

    public Filme(String nome, LocalDate data, int investimento, float bilheteira, List<String> financiadores, String genero) {
        this.nome = nome;
        this.data = data;
        this.investimento = investimento;
        this.bilheteira = bilheteira;
        this.financiadores = financiadores;
        this.genero = genero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getInvestimento() {
        return investimento;
    }

    public void setInvestimento(int investimento) {
        this.investimento = investimento;
    }

    public float getBilheteira() {
        return bilheteira;
    }

    public void setBilheteira(float bilheteira) {
        this.bilheteira = bilheteira;
    }

    public List<String> getfinanciadores() {
        return financiadores;
    }

    public void setfinanciadores(List<String> financiadores) {
        this.financiadores = financiadores;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }



}
public class Main {
    public static void main(String[] args) {
      //  Scanner scanner = new Scanner(System.in);

        LocalDateTime data = LocalDateTime.of(2025, Month.FEBRUARY, 21, 12, 0, 0, 0);
        LocalDate epoch = data.toInstant(ZoneOffset.UTC).toEpochMilli();

        System.out.println("Timestamp epoch (milli): " + epoch);

        Instant instant = Instant.ofEpochSecond(epoch);
        ZonedDateTime dateTime = instant.atZone(ZoneOffset.UTC);

        System.out.println("Data e hora correspondente ao timestamp: " + dateTime);


       // scanner.close();
    }
}