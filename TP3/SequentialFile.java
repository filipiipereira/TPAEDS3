/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Movie.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SequentialFile {
    private static String FILE_NAME = "SequentialFile.dat";
    private static final String BTREE_NAME = "tree.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";
    private static final String DICIONARYNAME_LIST_NAME = "dicionaryListName.dat";
    private static final String BLOCOSNAME_LIST_NAME = "blocosListName.dat";
    private static final String DICIONARYGENRE_LIST_NAME = "dicionaryGenre.dat";
    private static final String BLOCOSGENRE_LIST_NAME = "blocosListGenre.dat";
    private static final String COMPRESSED_HUFFMAN_PREFIX = "SequentialFileHuffManCompress_v";
    private static final String COMPRESSED_LZW_PREFIX = "SequentialFileLZWCompress_v";
    private static final String COMPRESSED_SUFFIX = ".dat";
    private static final String COMPRESSED_BTREE_HUFMANN_PREFIX = "TreeHuffManCompress_v";
    private static final String COMPRESSED_DIRECTORY_HASH_HUFMANN_PREFIX = "hashDirectoryHuffManCompress_v";
    private static final String COMPRESSED_BUCKET_HASH_HUFMANN_PREFIX = "hashBucketHuffManCompress_v";
    private static final String COMPRESSED_DICIONARYNAME_LIST_HUFMANN_PREFIX = "dicionaryListNameHuffManCompress_v";
    private static final String COMPRESSED_DICIONARYGENRE_LIST_HUFMANN_PREFIX = "dicionaryListGenreHuffManCompress_v";
    private static final String COMPRESSED_BLOCOSNAME_LIST_HUFMANN_PREFIX = "blocosListNameHuffManCompress_v";
    private static final String COMPRESSED_BLOCOSGENRE_LIST_HUFMANN_PREFIX = "blocosListaGenreHuffManCompress_v";
    private static final String COMPRESSED_BTREE_LZW_PREFIX = "TreeLZWCompress_v";
    private static final String COMPRESSED_DIRECTORY_HASH_LZW_PREFIX = "hashDirectoryLZWCompress_v";
    private static final String COMPRESSED_BUCKET_HASH_LZW_PREFIX = "hashBucketLZWCompress_v";
    private static final String COMPRESSED_DICIONARYNAME_LIST_LZW_PREFIX = "dicionaryListNameLZWCompress_v";
    private static final String COMPRESSED_DICIONARYGENRE_LIST_LZW_PREFIX = "dicionaryListGenreLZWCompress_v";
    private static final String COMPRESSED_BLOCOSNAME_LIST_LZW_PREFIX = "blocosListNameLZWCompress_v";
    private static final String COMPRESSED_BLOCOSGENRE_LIST_LZW_PREFIX = "blocosListaGenreLZWCompress_v";


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

    public static String CompressHuffman() {
        String nomeArquivo = null;
        try {
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
        int lengthOriginal = (int) raf.length();
        byte[] arrayBytes = new byte[lengthOriginal];

        for (int i = 0; i < lengthOriginal; i++) {
            arrayBytes[i] = raf.readByte();
        }
        //SEQUENTIAL FILE
        HashMap<Byte, String> codigos = Huffman.geraCodigos(arrayBytes);

        byte[] vb = Huffman.codifica(arrayBytes, codigos);
        //System.out.println(vb);
        int versao = contarVersoes(COMPRESSED_HUFFMAN_PREFIX, COMPRESSED_SUFFIX);
        nomeArquivo = COMPRESSED_HUFFMAN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
        escreveArquivoHuff(nomeArquivo, codigos, vb);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    //     //BTREE
    //     try {
    //     RandomAccessFile rafBTREE = new RandomAccessFile(BTREE_NAME, "r");
    //     int lengthOriginal = (int) rafBTREE.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBTREE.readByte();
    //     }
    //     HashMap<Byte, String> codigosBtree = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb1 = Huffman.codifica(arrayBytes, codigosBtree);
    //     int versao = contarVersoes(COMPRESSED_BTREE_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_BTREE_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBtree, vb1);
    //     rafBTREE.close();
    // } catch (Exception e) {
    //     e.printStackTrace();
    // }

    // //HashDirectory
    //     try {
    //     RandomAccessFile rafDirectory = new RandomAccessFile(DIRECTORY_HASH, "r");
    //     int lengthOriginal = (int) rafDirectory.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafDirectory.readByte();
    //     }
    //     HashMap<Byte, String> codigosDirectory = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosDirectory);
    //     int versao = contarVersoes(COMPRESSED_DIRECTORY_HASH_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_DIRECTORY_HASH_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosDirectory, vb);
    //     rafDirectory.close();
    // } catch (Exception e) {
    //     e.printStackTrace();
    // }

    //  //HashBucket
    //     try {
    //     RandomAccessFile rafBucket = new RandomAccessFile(BUCKET_HASH, "r");
    //     int lengthOriginal = (int) rafBucket.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBucket.readByte();
    //     }
    //     HashMap<Byte, String> codigosBucket = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosBucket);
    //     int versao = contarVersoes(COMPRESSED_BUCKET_HASH_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_BUCKET_HASH_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBucket, vb);
    //     rafBucket.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
        
    //     //DicionaryNameList
    //     try {
    //     RandomAccessFile rafBucket = new RandomAccessFile(DICIONARYNAME_LIST_NAME, "r");
    //     int lengthOriginal = (int) rafBucket.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBucket.readByte();
    //     }
    //     HashMap<Byte, String> codigosBucket = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosBucket);
    //     int versao = contarVersoes(COMPRESSED_DICIONARYNAME_LIST_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_DICIONARYNAME_LIST_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBucket, vb);
    //     rafBucket.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //DicionaryGenreList
    //     try {
    //     RandomAccessFile rafBucket = new RandomAccessFile(DICIONARYGENRE_LIST_NAME, "r");
    //     int lengthOriginal = (int) rafBucket.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBucket.readByte();
    //     }
    //     HashMap<Byte, String> codigosBucket = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosBucket);
    //     int versao = contarVersoes(COMPRESSED_DICIONARYGENRE_LIST_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_DICIONARYGENRE_LIST_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBucket, vb);
    //     rafBucket.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //blocoNomesList
    //     try {
    //     RandomAccessFile rafBucket = new RandomAccessFile(BLOCOSNAME_LIST_NAME, "r");
    //     int lengthOriginal = (int) rafBucket.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBucket.readByte();
    //     }
    //     HashMap<Byte, String> codigosBucket = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosBucket);
    //     int versao = contarVersoes(COMPRESSED_BLOCOSNAME_LIST_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_BLOCOSNAME_LIST_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBucket, vb);
    //     rafBucket.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //BlocosGenreList
    //     try {
    //     RandomAccessFile rafBucket = new RandomAccessFile(BLOCOSGENRE_LIST_NAME, "r");
    //     int lengthOriginal = (int) rafBucket.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = rafBucket.readByte();
    //     }
    //     HashMap<Byte, String> codigosBucket = Huffman.geraCodigos(arrayBytes);

    //     byte[] vb = Huffman.codifica(arrayBytes, codigosBucket);
    //     int versao = contarVersoes(COMPRESSED_BLOCOSGENRE_LIST_HUFMANN_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivobtree = COMPRESSED_BLOCOSGENRE_LIST_HUFMANN_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;
    //     escreveArquivoHuff(nomeArquivobtree, codigosBucket, vb);
    //     rafBucket.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    return nomeArquivo;
}


    private static void escreveArquivoHuff(String nomeArquivo, HashMap<Byte, String> codigos, byte[] vb) {
        try {
            FileOutputStream fos2 = new FileOutputStream(nomeArquivo);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            
            oos2.writeObject(codigos); // salva o dicionário
            oos2.writeInt(vb.length); // número de bits válidos em vb
            oos2.write(vb); // dados comprimidos
            oos2.close();
        } catch (Exception e) {
            
        }
        
    }


public static void DecompressHuffman(String nomeArquivo) {
    try {
        FileInputStream fis = new FileInputStream(nomeArquivo);
        ObjectInputStream ois = new ObjectInputStream(fis);

        HashMap<Byte, String> codigos = (HashMap<Byte, String>) ois.readObject();
        int total = ois.readInt();
        byte[] vb = new byte[total];
        for(int i=0;i<total;i++) {
            vb[i] = ois.readByte();
        }
        fis.close();
        ois.close();

        VetorDeBits sequenciaCodificada = new VetorDeBits(vb);
        String arquivoComprimido = sequenciaCodificada.toString();

        byte[] descomprimido = Huffman.decodifica(arquivoComprimido, codigos);
        
        FileOutputStream fos = new FileOutputStream(FILE_NAME);

        fos.write(descomprimido);

        fos.close();
        // boolean achou = false;
        // int versao;
        // for(int i=nomeArquivo.length()-1;i>0 && !achou;i--) {
        //     if (Character.isDigit(nomeArquivo.charAt(i))) {
        //         versao = nomeArquivo.charAt(i);
        //         achou = true; 
        // }


        } catch (Exception e) {
            e.printStackTrace();
        }
}

    public static String CompressLZW() {
        String nomeArquivo = null;
        try {
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
        int lengthOriginal = (int) raf.length();
        byte[] arrayBytes = new byte[lengthOriginal];

        for (int i = 0; i < lengthOriginal; i++) {
            arrayBytes[i] = raf.readByte();
        }

        byte[] arqCodificado = LZW.codifica(arrayBytes);

        int versao = contarVersoes(COMPRESSED_LZW_PREFIX, COMPRESSED_SUFFIX);
        nomeArquivo = COMPRESSED_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

        escreveArquivoLZW(nomeArquivo, arqCodificado);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    //     //BTREE
    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(BTREE_NAME, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_BTREE_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_BTREE_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //HASHDIRECTORY
    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(DIRECTORY_HASH, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_DIRECTORY_HASH_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_DIRECTORY_HASH_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //HASHBUCKET

    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(BUCKET_HASH, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_BUCKET_HASH_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_BUCKET_HASH_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //DICIONARYNAMELIST

    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(DICIONARYNAME_LIST_NAME, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_DICIONARYNAME_LIST_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_DICIONARYNAME_LIST_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //DICIONARYGENRELIST
    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(DICIONARYGENRE_LIST_NAME, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_DICIONARYGENRE_LIST_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_DICIONARYGENRE_LIST_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //BLOCOSNAMELIST

    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(BLOCOSNAME_LIST_NAME, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_BLOCOSNAME_LIST_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_BLOCOSNAME_LIST_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     //BLOCOSGENRELIST

    //     try {
    //     RandomAccessFile raf = new RandomAccessFile(BLOCOSGENRE_LIST_NAME, "r");
    //     int lengthOriginal = (int) raf.length();
    //     byte[] arrayBytes = new byte[lengthOriginal];

    //     for (int i = 0; i < lengthOriginal; i++) {
    //         arrayBytes[i] = raf.readByte();
    //     }

    //     byte[] arqCodificado = LZW.codifica(arrayBytes);

    //     int versao = contarVersoes(COMPRESSED_BLOCOSGENRE_LIST_LZW_PREFIX, COMPRESSED_SUFFIX);
    //     String nomeArquivo2 = COMPRESSED_BLOCOSGENRE_LIST_LZW_PREFIX + (versao + 1) + COMPRESSED_SUFFIX;

    //     escreveArquivoLZW(nomeArquivo2, arqCodificado);
        
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

        return nomeArquivo;
    }

    public static void escreveArquivoLZW(String nomeArquivo, byte[] arqCodificado) {
        try {
            FileOutputStream fos = new FileOutputStream(nomeArquivo);
            fos.write(arqCodificado);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DecompressLZW(String nomeArquivo) {
        try {  
            FileInputStream fis = new FileInputStream(nomeArquivo);
            
            byte[] arqComprimido = fis.readAllBytes();

            byte[] arqDecodificado = LZW.decodifica(arqComprimido);

            FileOutputStream fos = new FileOutputStream(FILE_NAME);

            fos.write(arqDecodificado);

            fis.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void compararAlgoritmoCompressao(String nomeArquivoHuff, String nomeArquivoLZW, long resultadoHuff, long resultadoLZW) {
        try {
            RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
            int lengthOriginal = (int) raf.length();
            raf.close();
            File fileLZW = new File(nomeArquivoLZW);
            int lengthCompressLZW = (int) fileLZW.length();
            File fileHuff = new File(nomeArquivoHuff);
            int lengthCompressHuff = (int) fileHuff.length();


        System.out.println("=====> COMPRESSÃO HUFFMAN <=====");
        long resultadoSegHuff = (resultadoHuff) / 1000;
        System.out.println("Tempo de Execução da Compressão Huffman: " + resultadoSegHuff + " segundo(s)" + " ou " + resultadoHuff + " milissegundo(s)");
        System.out.printf("Tamanho arquivo original: %d\n", lengthOriginal);
        System.out.printf("Tamanho arquivo comprimido: %d\n", lengthCompressHuff);
        float taxaCompressaoHuff = (float) lengthCompressHuff / lengthOriginal;
        System.out.printf("Taxa de Compressão: %.3f\n",  taxaCompressaoHuff);
        float fatorCompressaoHuff = (float) lengthOriginal / lengthCompressHuff;
        System.out.printf("Fator de Compressão: %.3f\n", fatorCompressaoHuff);
        double ganhoHuff = 100 * Math.log((float) lengthOriginal / lengthCompressHuff);
        System.out.printf("Ganho de Compresão: %.3f\n", ganhoHuff);
        float percentualReducaoHuff = 100 * ( (float) 1 - taxaCompressaoHuff);
        System.out.printf("Percentual de Compressão: %.3f%%\n",percentualReducaoHuff);
        System.out.println("");
        System.out.println("=====> COMPRESSÃO LZW <=====");
        long resultadoLZWSeg = (resultadoLZW) / 1000;
        System.out.println("Tempo de Execução da Compressão LZW: " + resultadoLZWSeg + " segundos" + " ou " + resultadoLZW + " milissegundos");
        System.out.printf("Tamanho arquivo original: %d\n", lengthOriginal);
        System.out.printf("Tamanho arquivo comprimido: %d\n", lengthCompressLZW);
        float taxaCompressaoLZW = (float) lengthCompressLZW / lengthOriginal;
        System.out.printf("Taxa de Compressão: %.3f\n",  taxaCompressaoLZW);
        float fatorCompressaoLZW = (float) lengthOriginal / lengthCompressLZW;
        System.out.printf("Fator de Compressão: %.3f\n", fatorCompressaoLZW);
        double ganhoLZW = 100 * Math.log((float) lengthOriginal / lengthCompressLZW);
        System.out.printf("Ganho de Compresão: %.3f\n", ganhoLZW);
        float percentualReducaoLZW = 100 * ( (float) 1 - taxaCompressaoLZW);
        System.out.printf("Percentual de Compressão: %.3f%%\n",percentualReducaoLZW);
        System.out.println("");
        System.out.println("=====> RESUMO ESPAÇO(quantidade de bytes) <=====");
        if(lengthCompressHuff < lengthCompressLZW) System.out.println("O algoritmo Huffman foi mais eficiente na questão de espaço nesse caso.");
        else if(lengthCompressLZW < lengthCompressHuff) System.out.println("O algoritmo LZW foi mais eficiente na questão de espaço nesse caso.");
        else System.out.println("Os dois algoritmos foram exatamente os mesmos na questão de espaço nesse caso.");
        System.out.println("");
        System.out.println("=====> RESUMO PERCENTUAL DE COMPRESSÃO <=====");
        if(percentualReducaoHuff > percentualReducaoLZW) System.out.println("O percentual de compressão do Huffman nesse caso foi superior.");
        else if(percentualReducaoLZW > percentualReducaoHuff)  System.out.println("O percentual de compressão do LZW nesse caso foi superior.");
        else System.out.println("O percentual de compressão entre os algoritmos foi exatamente o mesmo.");
        System.out.println("");
        System.out.println("=====> RESUMO TEMPO DE EXECUÇÃO <=====");
        if(resultadoHuff < resultadoLZW) System.out.println("Nesse caso o tempo de execução do Huffman foi mais eficiente.");
        else if(resultadoLZW < resultadoHuff)  System.out.println("Nesse caso o tempo de execução do LZW foi mais eficiente.");
        else System.out.println("Nesse caso o tempo de execução entre os algoritmos foi exatamente o mesmo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    public static void compararAlgoritmoDescompressao(long resultadoHuff, long resultadoLZW) {
        try { 
        System.out.println("=====> DESCOMPRESSÃO HUFFMAN <=====");
        long resultadoSegHuff = (resultadoHuff) / 1000;
        System.out.println("Tempo de Execução da Compressão Huffman: " + resultadoSegHuff + " segundo(s)" + " ou " + resultadoHuff + " milissegundo(s)");
        System.out.println("");
        System.out.println("=====> DESCOMPRESSÃO LZW <=====");
        long resultadoLZWSeg = (resultadoLZW) / 1000;
        System.out.println("Tempo de Execução da Compressão Huffman: " + resultadoLZWSeg + " segundo(s)" + " ou " + resultadoLZW + " milissegundo(s)");
        System.out.println("");
        System.out.println("=====> RESUMO TEMPO DE EXECUÇÃO <=====");
        if(resultadoHuff < resultadoLZW) System.out.println("Nesse caso o tempo de execução do Huffman foi mais eficiente.");
        else if(resultadoLZW < resultadoHuff)  System.out.println("Nesse caso o tempo de execução do LZW foi mais eficiente.");
        else System.out.println("Nesse caso o tempo de execução entre os algoritmos foi exatamente o mesmo.");
        System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static int contarVersoes(String prefixo, String sufixo) {
    File pasta = new File(".");
    File[] arquivos = pasta.listFiles();
    int contador = 0;
    if (arquivos != null) {
        for (File f : arquivos) {
            if (f.getName().startsWith(prefixo) && f.getName().endsWith(sufixo)) {
                contador++;
            }
        }
    }
    return contador;
}

    public static void KMP(String padrao) {
        StringBuilder texto = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String linha;
            while ((linha = br.readLine()) != null) {
                texto.append(linha).append("\n");
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

       // System.out.println(texto.toString());

        KMP.procuraPadrao(texto.toString(), padrao);
    }
}