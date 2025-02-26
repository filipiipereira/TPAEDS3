
import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller{
    public Film Form(Scanner scanner){
        System.out.print("Name: ");
        scanner.nextLine();//cleaning buffer
        String name = scanner.nextLine();
        System.out.println("Date(yyyy-MM-dd): ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(scanner.nextLine(), formatter);
        long epochDate = date.atStartOfDay(UTC).toEpochSecond();
        System.out.print("Budget: ");
        int budget = scanner.nextInt();
        System.out.print("Global box-office: ");
        float boxOffice = scanner.nextFloat();
        System.out.print("Genre: ");
        scanner.nextLine();//cleaning buffer
        String genre = scanner.nextLine();
        System.out.print("Financing companies(type '0' to stop): ");
        List<String> financingCompanies = new ArrayList<>();
        String financingCompany = scanner.nextLine();
        do { 
            financingCompanies.add(financingCompany);
            financingCompany = scanner.nextLine();
        } while (!financingCompany.equals("0"));
        Film film = new Film(0, name, epochDate, budget, boxOffice, financingCompanies, genre);
        return film;
    }
    public boolean Insert(Scanner scanner){
        boolean response = false;
        SequentialFile sf = new SequentialFile();
        return(sf.Insert(Form(scanner)));
    }
    public boolean Update(Scanner scanner){
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile sf = new SequentialFile();
        sf.Get(id).toStr();
        Film film = Form(scanner);
        film.setId(id);
        return(sf.Update(film));
    }
    public boolean Delete(Scanner scanner){
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile sf = new SequentialFile();
        return(sf.Delete(id));
    }
    public void Get(Scanner scanner){
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile sf = new SequentialFile();
        Film film = sf.Get(id);
        if(film == null) System.out.println("Film not found");
        else film.toStr();
    }
}