/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Movie.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.File;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
            System.out.println(genre.length());
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
            e.printStackTrace();
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
            long pos = IndexController.getPos(id,index);
            file.seek(pos); // Pula o último ID salvo
            Byte flag = file.readByte();
            int registerLength = file.readInt();
            if (flag != '*') {
                movie = ReadMovie(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * Atualiza um movie existente no arquivo sequencial.
     * @param newMovie Novo objeto Movie atualizado.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */

    public static boolean Update(Movie newMovie, int index) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            ArrayList<ParIntLong> lista = bTree.read(new ParIntLong(newMovie.getId(), -1));
            long pos;
            try {
                pos = IndexController.getPos(newMovie.getId(),index);
                file.seek(pos); // Pula o último ID salvo
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                if (flag != '*') {
                        int newSize = newMovie.registerByteSize();
                        if (newSize <= registerLength) {
                            file.seek(pos);
                            WriteMovieSmallerSizeUpdate(file, newMovie, registerLength);
                        } else {
                            file.seek(pos);
                            file.writeByte('*'); // Marcar como excluído
                            file.seek(file.length());
                            long newPos = file.getFilePointer();
                            WriteMovie(file, newMovie);
                            IndexController.Update(newPos, newMovie.getId(), index);
                        }
                        response = true;
                }
            } catch (Exception e) {
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
    
    public static boolean Delete(int id) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long position = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag == 0) {
                    int readID = file.readInt();
                    if (readID == id) {
                        file.seek(position);
                        file.writeByte('*');
                        numberOfMovies--;
                        response = true;
                    } else {
                        file.seek(position + 1 + 4 + registerSize);
                    }
                } else {
                    file.seek(position + 1 + 4 + registerSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Realiza a ordenação externa do arquivo principal.
     * 
     * @param b Número de registros por bloco.
     * @param m Número de caminhos de intercalação.
     */

    public static void ExternalSort(int b, int m){
        Distribution(b, m);
        String orderedFile = Intercalation(m, b);
        UpdateMainFile(orderedFile);
    }

    /**
     * Distribui os registros do arquivo principal em arquivos temporários ordenados.
     * 
     * @param b Número de registros por bloco.
     * @param m Número de caminhos de intercalação.
     */

    public static void Distribution(int b, int m){
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int quantityRecords;
            int quantityPaths;
            file.seek(4); //jumping lastId record
            boolean startingFile = true;
            while(file.getFilePointer() < file.length()){
                quantityPaths = 0;
                while(quantityPaths < m && file.getFilePointer() < file.length()){
                    Movie blockMovie[] = new Movie[b];
                    quantityRecords = 0;
                    while(quantityRecords < b && file.getFilePointer() < file.length()){
                        //reading record
                        byte flag = file.readByte();
                        int registerLength = file.readInt(); 
                        long pos = file.getFilePointer();
                        if(flag == '*'){
                            file.seek(pos + registerLength);
                        }
                        else{ 
                            Movie Movie = ReadMovie(file);
                            blockMovie[quantityRecords] = Movie;
                            quantityRecords++;
                            file.seek(pos + registerLength);
                        }
                    }
                    try (RandomAccessFile tempFile = new RandomAccessFile(FIRST_TEMPFILE_NAME + (quantityPaths+1), "rw")) {
                        if(file.getFilePointer() >= file.length()){
                            ArrayList<Movie> listBlockMovie = new ArrayList<>();
                            for(Movie Movie : blockMovie){
                                if(Movie != null){
                                    listBlockMovie.add(Movie);
                                }
                            }
                            Movie[] newBlockMovie = listBlockMovie.toArray(new Movie[0]);
                            Arrays.sort(newBlockMovie, Comparator.comparingInt(movie -> movie.getId()));
                            for(int i = 0; i < newBlockMovie.length; i++){
                                tempFile.seek(tempFile.length());
                                WriteMovie(tempFile, newBlockMovie[i]);
                            }
                        }
                        else{
                            Arrays.sort(blockMovie, Comparator.comparingInt(movie -> movie.getId()));
                            for(int i = 0; i < blockMovie.length; i++){
                                tempFile.seek(tempFile.length());
                                WriteMovie(tempFile, blockMovie[i]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    quantityPaths++;
                    startingFile = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza a intercalação dos arquivos temporários até obter o arquivo ordenado final.
     * 
     * @param m Número de caminhos de intercalação.
     * @param b Número de registros por bloco.
     * @return Nome do arquivo ordenado final.
     */

    private static String FIRST_TEMPFILE_NAME = "a";
    private static String SECOND_TEMPFILE_NAME = "b";

    public static String Intercalation(int m, int b){
        int currentNumberOfFiles = m;
        int currentNumberOfRecordsPerBlock = b;
        int indexOfIntercalation = 0;

        while(currentNumberOfFiles > 1){
            
            //controla os nomes dos arquivos(par ou impar)
            indexOfIntercalation++;
            String nameCurrentFiles;
            String nameNextFiles;
            if(indexOfIntercalation % 2 == 1){
                nameCurrentFiles = FIRST_TEMPFILE_NAME;
                nameNextFiles = SECOND_TEMPFILE_NAME;
            } 
            else{
                nameCurrentFiles = SECOND_TEMPFILE_NAME;
                nameNextFiles = FIRST_TEMPFILE_NAME;
            }

            //proximo numero de arquivos calculo
            int nextNumberOfFiles = (int)Math.ceil((double)numberOfMovies / (currentNumberOfRecordsPerBlock * currentNumberOfFiles));
            if(nextNumberOfFiles > currentNumberOfFiles) nextNumberOfFiles = currentNumberOfFiles;

            RandomAccessFile[] currentFiles = new RandomAccessFile[currentNumberOfFiles];
            RandomAccessFile[] nextFiles = new RandomAccessFile[nextNumberOfFiles];

            // Abrir todos os arquivos atuais
            for (int i = 0; i < currentNumberOfFiles; i++) {
                try{
                    currentFiles[i] = new RandomAccessFile(nameCurrentFiles + (i +1), "rw");    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < nextNumberOfFiles; i++) {
                try{
                    nextFiles[i] = new RandomAccessFile(nameNextFiles + (i + 1), "rw");   
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            //iniciailizdo vetores que guardam os ponteiro dos arquivos
            long currentPositions[] = new long[currentNumberOfFiles];
            for(int i = 0; i < currentNumberOfFiles; i++){
                currentPositions[i] = 0;
            }
            long nextPositions[] = new long[nextNumberOfFiles];
            for(int i = 0; i < nextNumberOfFiles; i++){
                nextPositions[i] = 0;
            }
            //para cada intercalacao le se o numero total de registros
            int numberOfReadRecords = 0;
            boolean intercalationIsOver = false;
            while(numberOfReadRecords < numberOfMovies && !intercalationIsOver){
                
                //cada secao vai ser escrita em um caminho diferente
                int indexOfPath = 0;                
                while(indexOfPath < nextNumberOfFiles && !intercalationIsOver){

                    //cada arquivo so pode ser lido em blocos
                    //inicializacao do vetor que controla ate onde pode ser lido
                    int numberOfReadRecordsPerCurrentFile[] = new int[currentNumberOfFiles];
                    for(int i = 0; i < currentNumberOfFiles; i++){
                        numberOfReadRecordsPerCurrentFile[i] = 0;
                    }

                    //para cada parte da intercalacao le se o numero de registros da secao
                    int numberOfRecordsReadPerSection = 0;
                    while(numberOfRecordsReadPerSection < currentNumberOfFiles*currentNumberOfRecordsPerBlock  && !intercalationIsOver){


                        //iniciando as leituras para achar o arquivo que possui o menor id
                        int smallestId = Integer.MAX_VALUE;
                        int smallestIdFile = -1;
                        for (int i = 0; i < currentNumberOfFiles; i++) {
                            try{
                                RandomAccessFile currentFile = currentFiles[i];
                                currentFile.seek(currentPositions[i] + 5);
                                //condicoes para leitura, o arquivo nao ter lido o bloco inteiro e o arquivo nao tiver acabado
                                if (numberOfReadRecordsPerCurrentFile[i] < currentNumberOfRecordsPerBlock && currentFile.getFilePointer() < currentFile.length() && numberOfReadRecords < numberOfMovies){
                                    int id = currentFile.readInt();
                                    if (id < smallestId) {
                                        smallestId = id;
                                        smallestIdFile = i;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //ler o movie de menor id e marcar a posicao do arquivo
                        Movie movie = null;
                        if(smallestIdFile != -1){
                            try{
                                RandomAccessFile currentFile = currentFiles[smallestIdFile];
                                currentFile.seek(currentPositions[smallestIdFile] + 5);//5 = byte flag + register size;
                                if(currentFile.getFilePointer() < currentFile.length()){
                                    movie = ReadMovie(currentFile);
                                    currentPositions[smallestIdFile] = currentFile.getFilePointer();
                                    numberOfReadRecordsPerCurrentFile[smallestIdFile]++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //escrever o movie no arquivo proximo na posicao quantidade caminhos proximos

                        if(movie != null){
                            try{
                                RandomAccessFile nextFile = nextFiles[indexOfPath];
                                nextFile.seek(nextPositions[indexOfPath]);
                                WriteMovie(nextFile, movie);
                                nextPositions[indexOfPath] += 5 + movie.registerByteSize(); 
                                //aumentando um no contador de registros
                                numberOfRecordsReadPerSection++;   
                                numberOfReadRecords++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else intercalationIsOver = true;
                    }
                    indexOfPath++;
                }  
            }
            if(currentNumberOfRecordsPerBlock * currentNumberOfFiles >= numberOfMovies) currentNumberOfRecordsPerBlock = numberOfMovies;
            else currentNumberOfRecordsPerBlock *= currentNumberOfFiles;
            currentNumberOfFiles = nextNumberOfFiles;

            // Fechar todos os arquivos no final
            for (RandomAccessFile file : currentFiles) {
                try{
                    file.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for (RandomAccessFile file : nextFiles) {
                try{
                    file.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        //deleting files
        File[] files1 = new File[m];
        File[] files2 = new File[m];
        for (int i = 1; i < m; i++) {
            files1[i] = new File(FIRST_TEMPFILE_NAME + (i + 1));    
            files2[i] = new File(SECOND_TEMPFILE_NAME + (i + 1));   
            files1[i].delete();
            files2[i].delete();
        }
        String OrderedFile;
        if(indexOfIntercalation % 2 == 1){
            OrderedFile = SECOND_TEMPFILE_NAME + 1;
            File file = new File(FIRST_TEMPFILE_NAME + 1);
            file.delete();
        }
        else{
            OrderedFile = FIRST_TEMPFILE_NAME + 1;
            File file = new File(SECOND_TEMPFILE_NAME + 1);
            file.delete();
        }
        return OrderedFile;
    }

    /**
     * Atualiza o arquivo principal com os registros ordenados.
     * 
     * @param FILE Nome do arquivo ordenado.
     */

    public static void UpdateMainFile(String FILE){
        Movie movie = null;
        File f = new File(FILE_NAME);
        f.delete();
        boolean firstFilm = true;
        try (RandomAccessFile orderedFile = new RandomAccessFile(FILE, "r")){
            try (RandomAccessFile mainFile = new RandomAccessFile(FILE_NAME, "rw")){
                orderedFile.seek(0);
                while(orderedFile.getFilePointer() < orderedFile.length()){
                    long position = orderedFile.getFilePointer();
                    orderedFile.seek(position + 5);//byte+int
                    movie = ReadMovie(orderedFile);
                    if(firstFilm){
                        mainFile.seek(4);
                        firstFilm = false;
                    }
                    else{
                        mainFile.seek(mainFile.length());
                    }
                    WriteMovie(mainFile, movie);
                }
                mainFile.seek(0);
                mainFile.writeInt(movie.getId());  
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = new File(FILE);
        file.delete();
    }
}