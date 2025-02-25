import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class LoadCsv{
    private String name;
    public LoadCsv(){
        this.name = "filmsDataSet.csv";
    }
    public void LoadFromCsv(){
        String linha;
        SequentialFile sequentialFile = new SequentialFile();
        try(BufferedReader br = new BufferedReader(new FileReader(name))){
            br.readLine(); //ignores the first one(header)
            linha = br.readLine();
            while(linha != null){
                String[] originalValues = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String[] values = new String[6];
                for (int i = 0; i < originalValues.length; i++) {
                    values[i] = originalValues[i];
                }
                if(originalValues.length < 6){
                    for(int i = originalValues.length; i < 6; i++){
                        values[i] = "";
                    }
                }
                System.out.println(values[0]);
                String name = values[0];
                LocalDate date = null;
                long epochDate = -1;
                if (!values[1].isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    date = LocalDate.parse(values[1], formatter);
                    epochDate = date.atStartOfDay(UTC).toEpochSecond();
                }
                int budget = 0;
                if(!values[2].isEmpty()){
                    budget = Integer.parseInt(values[2]);
                }
                float boxOffice = 0;
                if(!values[3].isEmpty()){
                    boxOffice = Float.parseFloat(values[3]);
                }
                String genre = values[4];
                List<String> financingCompanies = new ArrayList<>(Arrays.asList(values[5].split(",")));
                Film film = new Film(0, name, epochDate, budget, boxOffice, financingCompanies, genre);
                if(sequentialFile.Insert(film)) System.out.println("OK (Insert)");
                else System.out.println("ERROR (Insert)");
                linha= br.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}