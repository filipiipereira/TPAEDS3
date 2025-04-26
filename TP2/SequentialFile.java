/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Movie.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class SequentialFile {
    private static String FILE_NAME = "SequentialFile.dat";
    private static final String BTREE_NAME = "tree.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";

    private static int numberOfMovies = 0; 

    public static void WriteMovie(RandomAccessFile file, Movie Movie){
        try {
            file.writeByte(0); // flag
            file.writeInt(Movie.registerByteSize());
            file.writeInt(Movie.getId());
            file.writeUTF(Movie.getName());
            if(Movie.getDate() != null){
                file.writeLong(Movie.getDate().toEpochDay());
            }
            else{
                file.writeLong(0);
            }
            file.writeInt(Movie.getBudget());
            file.writeFloat(Movie.getBoxOffice());
            
            // Escreve o gênero como string de tamanho fixo
            String genre = Movie.getGenre();
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            
            file.writeInt(Movie.getFinancingCompanies().size());
            for (String company : Movie.getFinancingCompanies()) {
                file.writeUTF(company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void WriteMovieSmallerSizeUpdate(RandomAccessFile file, Movie Movie , int oldSize){
        try {
            file.writeByte(0); // flag
            file.writeInt(oldSize);
            file.writeInt(Movie.getId());
            file.writeUTF(Movie.getName());
            file.writeLong(Movie.getDate().toEpochDay());
            file.writeInt(Movie.getBudget());
            file.writeFloat(Movie.getBoxOffice());
            
            // Escreve o gênero como string de tamanho fixo
            String genre = Movie.getGenre();
            //System.out.println(genre.length());
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            
            file.writeInt(Movie.getFinancingCompanies().size());
            for (String company : Movie.getFinancingCompanies()) {
                file.writeUTF(company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Movie ReadMovie(RandomAccessFile file){
        Movie Movie = null;
        try {
            int registerId = file.readInt();
            String registerName = file.readUTF();
            long epochDay = file.readLong();
            LocalDate date = LocalDate.ofEpochDay(epochDay);
            int registerBudget = file.readInt();
            float registerBoxOffice = file.readFloat();
            byte[] genreBytes = new byte[10];
            file.readFully(genreBytes);
            String registerGenre = new String(genreBytes).trim();
            int numCompanies = file.readInt();
            List<String> registerFinancingCompanies = new ArrayList<>();
            for (int i = 0; i < numCompanies; i++) {
                registerFinancingCompanies.add(file.readUTF());
            }
            Movie = new Movie(registerId, registerName, date, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
        } catch (Exception e) {
        }
        return Movie;
    }

    /**
     * Insere um novo movie no arquivo sequencial.
     * @param Movie Objeto Movie a ser inserido.
     * @return true se a inserção for bem-sucedida, false caso contrário.
     */

    public static long InsertMovieFromCSV(Movie Movie, RandomAccessFile file) {
        long position = 0;
        try{
            int objectId;
            if (file.length() != 0) {
                file.seek(0);
                int lastId = file.readInt();
                objectId = lastId + 1;
            } else {
                objectId = 1;
            }
            Movie.setId(objectId);
            file.seek(0);
            file.writeInt(Movie.getId());
            file.seek(file.length());
            position = file.getFilePointer();
            WriteMovie(file, Movie);
            numberOfMovies++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    /**
     * Recupera um movie do arquivo sequencial pelo ID.
     * @param id Identificador do movie.
     * @return Objeto Movie correspondente ou null se não encontrado.
     */

    public static Movie Get(int id, int index) {
        Movie movie = null;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            long pos = IndexController.GetPos(id, index);
            file.seek(pos);
            Byte flag = file.readByte();
            int registerLength = file.readInt();
            if (flag != '*') {
                movie = ReadMovie(file);
            }
        } catch (Exception e) {
        }
        return movie;
    }

     public static Movie[] GetLista(String palavra, String palavra2, int option) {
        Movie[] movies = null;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            ElementoLista[] lista = IndexController.GetPosLista(palavra,palavra2, option);
            movies = new Movie[lista.length];
            for(int i = 0; i < lista.length; i++){
                file.seek(lista[i].getposition());
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                if (flag != '*') {
                    movies[i] = ReadMovie(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Atualiza um movie existente no arquivo sequencial.
     * @param newMovie Novo objeto Movie atualizado.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */

    public static boolean Update(Movie newMovie, int index) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            long pos; 
            Movie oldMovie = null;
            if(index == 3){
                pos = IndexController.GetPos(newMovie.getId(), 2); 
                file.seek(pos);
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                oldMovie = ReadMovie(file);
            }
            else{
                pos = IndexController.GetPos(newMovie.getId(), index);//usa a arvore ou hash dependendo da escolha
            }
            file.seek(pos); 
            Byte flag = file.readByte();
            int registerLength = file.readInt();
            if (flag != '*') {
                int newSize = newMovie.registerByteSize();
                if (newSize <= registerLength) {
                    file.seek(pos);
                    WriteMovieSmallerSizeUpdate(file, newMovie, registerLength);
                    IndexController.InvertedListUpdate(oldMovie, newMovie, pos);
                } else {
                    file.seek(pos);
                    file.writeByte('*'); // Marcar como excluído
                    file.seek(file.length());
                    long newPos = file.getFilePointer();
                    WriteMovie(file, newMovie);
                    IndexController.Update(newPos,newMovie.getId());//hash e arvore
                    IndexController.InvertedListUpdate(oldMovie, newMovie, newPos);
                }
                response = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    /**
     * Exclui um movie do arquivo sequencial pelo ID.
     * @param id Identificador do movie a ser excluído.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
    
    public static boolean Delete(int id, int index) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            Movie deletedMovie = null;
            long pos;
            if(index == 3){
                pos = IndexController.GetPos(id, 2); 
                file.seek(pos);
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                deletedMovie = ReadMovie(file);
            }
            else{
                pos = IndexController.GetPos(id, index); //usa a arvore ou hash dependendo da escolha
            }

            file.seek(pos);
            byte flag = file.readByte();
            int registerSize = file.readInt();
            if (flag == 0) {
                file.seek(pos);
                file.writeByte('*');
                numberOfMovies--;
                response =  IndexController.Delete(id) && IndexController.InvertedListDelete(id, deletedMovie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}