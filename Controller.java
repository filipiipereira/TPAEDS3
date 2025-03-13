import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Controller responsável por gerenciar a interação com o usuário e manipular objetos da classe movie.
 */
public class Controller {
    
    /**
     * Método responsável por capturar os dados de um moviee a partir da entrada do usuário.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto movie preenchido com os dados informados pelo usuário.
     */
    public static Movie Form(Scanner scanner) {
        System.out.print("Name: ");
        scanner.nextLine(); // Limpa o buffer
        String name = scanner.nextLine();

        System.out.print("Date(yyyy-MM-dd): ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(scanner.nextLine(), formatter);
        long epochDate = date.atStartOfDay(UTC).toEpochSecond();

        System.out.print("Budget: ");
        int budget = scanner.nextInt();

        System.out.print("Global box-office: ");
        float boxOffice = scanner.nextFloat();

        System.out.print("Genre: ");
        scanner.nextLine(); // Limpa o buffer
        String genre = scanner.nextLine();

        System.out.print("Financing companies (type '0' to stop): ");
        List<String> financingCompanies = new ArrayList<>();
        String financingCompany = scanner.nextLine();
        
        while (!financingCompany.equals("0")) {
            financingCompanies.add(financingCompany);
            financingCompany = scanner.nextLine();
        }

        return new Movie(1, name, epochDate, budget, boxOffice, financingCompanies, genre);
    }

    /**
     * Método responsável por inserir um novo moviee no arquivo sequencial.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a inserção foi bem-sucedida, false caso contrário.
     */
    public static boolean Insert(Scanner scanner) {
        return SequentialFile.Insert(Form(scanner));
    }

    /**
     * Método responsável por atualizar um moviee existente com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public static boolean Update(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile.Get(id).toStr();
        Movie movie = Form(scanner);
        movie.setId(id);
        return SequentialFile.Update(movie);
    }

    /**
     * Método responsável por deletar um moviee com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */
    public static boolean Delete(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Delete(id);
    }

    /**
     * Método responsável por buscar um moviee com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto movie correspondente ao ID informado.
     */
    public static Movie Get(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Get(id);
    }

    public static void Sort(Scanner scanner){
        System.out.print("Digite a quantidade de caminhos: ");
        int m = scanner.nextInt();
        System.out.print("Digite a quantidade de registros por bloco: ");
        int b = scanner.nextInt();
        SequentialFile.ExternalSort(b,m);
    }
}