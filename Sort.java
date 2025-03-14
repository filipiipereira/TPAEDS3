
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Sort extends SequentialFile{
    private static String TEMP_FILE_FIRST = "tempFile";
    private static String TEMP_FILE_SECOND = "tempFileAux";
    
    public static void ExternalSort(int b, int m){
        Distribution(b, m);
        Intercalation(m, b);
    }

    public static void Distribution(int b, int m){
        int totalRecords = 0;
        //starting files array
        RandomAccessFile tempFiles[] = new RandomAccessFile[m];
        for(int i = 0; i < m; i++){
            try {
                tempFiles[i] = new RandomAccessFile(TEMP_FILE_FIRST + (i+1), "rw");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int numberOfRecords;
            int numberOfPaths;
            file.seek(4); //jumping lastId record
            boolean startingFile = true;
            while(file.getFilePointer() < file.length()){
                numberOfPaths = 0;
                while(numberOfPaths < m && file.getFilePointer() < file.length()){
                    Movie blockMovie[] = new Movie[b];
                    numberOfRecords = 0;
                    while(numberOfRecords < b && file.getFilePointer() < file.length()){
                        //reading record
                        byte flag = file.readByte();
                        int registerLength = file.readInt(); 
                        long pos = file.getFilePointer();
                        if(flag == '*'){
                            file.seek(pos + registerLength);
                        }
                        else{ 
                            Movie Movie = ReadMovie(file);
                            blockMovie[numberOfRecords] = Movie;
                            numberOfRecords++;
                            totalRecords++;
                        }
                    }
                    try{
                        RandomAccessFile tempFile = tempFiles[numberOfPaths];
                        //if block is not completed, copy in an arrayList and write the sorted arraylist
                        if(file.getFilePointer() >= file.length()){
                            ArrayList<Movie> listBlockMovie = new ArrayList<>();
                            for(Movie Movie : blockMovie){
                                if(Movie != null){
                                    listBlockMovie.add(Movie);
                                }
                            }
                            listBlockMovie.sort(Comparator.comparingInt(movie -> movie.getId()));
                            for(int i = 0; i < listBlockMovie.size(); i++){
                                tempFile.seek(tempFile.length());
                                WriteMovie(tempFile, listBlockMovie.get(i));
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
                    numberOfPaths++;
                    startingFile = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //closing tempFiles
        for (RandomAccessFile tempFile : tempFiles) {
            try{
                tempFile.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void Intercalation(int m, int b){
        int currentNumberOfFiles = m;
        int numberOfRecordsPerBlock = b;
        int indexOfIntercalation = 0;

        while(currentNumberOfFiles > 1){
            
            //controla os nomes dos arquivos(par ou impar)
            indexOfIntercalation++;
            String nameOfCurrentFiles;
            String nameOfNextFiles;
            if(indexOfIntercalation % 2 == 1){
                nameOfCurrentFiles = TEMP_FILE_FIRST;
                nameOfNextFiles = TEMP_FILE_SECOND;
            } 
            else{
                nameOfCurrentFiles = TEMP_FILE_SECOND;
                nameOfNextFiles = TEMP_FILE_FIRST;
            }

            //proximo numero de arquivos calculo
            int nextNumberOfFiles = (int)Math.ceil((double)(numberOfMovies / (numberOfRecordsPerBlock * currentNumberOfFiles)));

            RandomAccessFile[] currentFiles = new RandomAccessFile[currentNumberOfFiles];
            RandomAccessFile[] nextFiles = new RandomAccessFile[nextNumberOfFiles];

            // Abrir todos os arquivos atuais
            for (int i = 0; i < currentNumberOfFiles; i++) {
                try{
                    currentFiles[i] = new RandomAccessFile(nameOfCurrentFiles + (i +1), "rw");    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < nextNumberOfFiles; i++) {
                try{
                    nextFiles[i] = new RandomAccessFile(nameOfNextFiles + (i + 1), "rw");   
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            //iniciailizdo vetores que guardam os ponteiro dos arquivos
            long currentFilesPositions[] = new long[currentNumberOfFiles];
            for(int i = 0; i < currentNumberOfFiles; i++){
                currentFilesPositions[i] = 0;
            }
            long nextFilesPositions[] = new long[nextNumberOfFiles];
            for(int i = 0; i < nextNumberOfFiles; i++){
                nextFilesPositions[i] = 0;
            }
            //para cada intercalacao le se o numero total de registros
            int totalReadRecords = 0;
            boolean intercalationIsOver = false;
            while(totalReadRecords < numberOfMovies && !intercalationIsOver){
                
                //cada secao vai ser escrita em um caminho diferente
                int whichNextFile = 0;                
                while(whichNextFile < nextNumberOfFiles && !intercalationIsOver){

                    //cada arquivo so pode ser lido em blocos
                    //inicializacao do vetor que controla ate onde pode ser lido
                    int numberOfReadRecordsPerCurrentFile[] = new int[currentNumberOfFiles];
                    for(int i = 0; i < currentNumberOfFiles; i++){
                        numberOfReadRecordsPerCurrentFile[i] = 0;
                    }

                    //para cada parte da intercalacao le se o numero de registros da secao
                    int recordsReadPerSection = 0;
                    while(recordsReadPerSection < currentNumberOfFiles*numberOfRecordsPerBlock  && !intercalationIsOver){

                        //iniciando as leituras para achar o arquivo que possui o menor id
                        int smallerId = Integer.MAX_VALUE;
                        int smallerIdFile = -1;
                        for (int i = 0; i < currentNumberOfFiles; i++) {
                            try{
                                RandomAccessFile currentFile = currentFiles[i];
                                currentFile.seek(currentFilesPositions[i] + 5);
                                //condicoes para leitura, o arquivo nao ter lido o bloco inteiro e o arquivo nao tiver acabado
                                if (numberOfReadRecordsPerCurrentFile[i] < numberOfRecordsPerBlock && currentFile.getFilePointer() < currentFile.length() && totalReadRecords < numberOfMovies){
                                    int id = currentFile.readInt();
                                    if (id < smallerId) {
                                        smallerId = id;
                                        smallerIdFile = i;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //ler o movie de menor id e marcar a posicao do arquivo
                        Movie movie = null;
                        if(smallerIdFile != -1){
                            try{
                                RandomAccessFile currentFile = currentFiles[smallerIdFile];
                                currentFile.seek(currentFilesPositions[smallerIdFile] + 5);//5 = byte flag + register size;
                                if(currentFile.getFilePointer() < currentFile.length()){
                                    movie = ReadMovie(currentFile);
                                    currentFilesPositions[smallerIdFile] = currentFile.getFilePointer();
                                    numberOfReadRecordsPerCurrentFile[smallerIdFile]++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //escrever o movie no arquivo proximo na posicao quantidade caminhos proximos

                        if(movie != null){
                            try{
                                RandomAccessFile nextFile = nextFiles[whichNextFile];
                                nextFile.seek(nextFilesPositions[whichNextFile]);
                                WriteMovie(nextFile, movie);
                                nextFilesPositions[whichNextFile] += 5 + movie.registerByteSize(); 
                                //aumentando um no contador de registros
                                recordsReadPerSection++;   
                                totalReadRecords++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else intercalationIsOver = true;
                    }
                    whichNextFile++;
                }  
            }
            if(numberOfRecordsPerBlock * currentNumberOfFiles >= numberOfMovies) numberOfRecordsPerBlock = numberOfMovies;
            else numberOfRecordsPerBlock *= currentNumberOfFiles;
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
        String arquivoOrdenado;
        if(indexOfIntercalation % 2 == 1){
            arquivoOrdenado = "TempFileAux1";
        }
        else{
            arquivoOrdenado = TEMP_FILE_FIRST;
        }
        System.out.println("Arquivo ordenado: " + arquivoOrdenado);
    }
}
