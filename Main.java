import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class Filme {
    private String nome;
    private long data;
    private float investimento;
    private float bilheteira;
    private String franquia;
    private String genero;

    public Filme() {

    }

    public Filme(String nome, long data, float investimento, float bilheteira, String franquia, String genero) {
        this.nome = nome;
        this.data = data;
        this.investimento = investimento;
        this.bilheteira = bilheteira;
        this.franquia = franquia;
        this.genero = genero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public float getInvestimento() {
        return investimento;
    }

    public void setInvestimento(float investimento) {
        this.investimento = investimento;
    }

    public float getBilheteira() {
        return bilheteira;
    }

    public void setBilheteira(float bilheteira) {
        this.bilheteira = bilheteira;
    }

    public String getFranquia() {
        return franquia;
    }

    public void setFranquia(String franquia) {
        this.franquia = franquia;
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
        long epoch = data.toInstant(ZoneOffset.UTC).toEpochMilli();

        System.out.println("Timestamp epoch (milli): " + epoch);

        Instant instant = Instant.ofEpochSecond(epoch);
        ZonedDateTime dateTime = instant.atZone(ZoneOffset.UTC);

        System.out.println("Data e hora correspondente ao timestamp: " + dateTime);


       // scanner.close();
    }
}