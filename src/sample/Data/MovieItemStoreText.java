package sample.Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

// clasa initiala pentru realizare stocare/incarcare in/din fisier .txt a datelor filmelor(complet functionala) - AM INLOCUIT-O CU STOCARE IN BAZA DE DATE
// am lasat metoda pentru salvare ca .tsv in aceasta clasa deoarece mi s-a parut sugestiv numele clasei

public class MovieItemStoreText {

    private static MovieItemStoreText instance=new MovieItemStoreText();
    private static String numeFisier = "ListaFilme.txt";

    private ObservableList<MovieItem> movieItemsList;

    public static MovieItemStoreText getInstance(){ return instance;}

    public ObservableList<MovieItem> getMovieItemsList() {
        return movieItemsList;
    }

    public void addMovieItem(MovieItem movieItem){
        movieItemsList.add(movieItem);
    }


    public void addIndexMovieItem(int index,MovieItem movieItem){ movieItemsList.add(index,movieItem);}

    public void removeIndexMovieItem(int index){ movieItemsList.remove(index);}


    public void removeMovieItem(MovieItem movieItem){ movieItemsList.remove(movieItem); }


    public void loadMovieItems() throws IOException{
        movieItemsList= FXCollections.observableArrayList();
        Path path = Paths.get(numeFisier);
        BufferedReader br= Files.newBufferedReader(path);

        String input;

        try{
            while ((input = br.readLine()) != null) {
                String[] campuri=input.split("\t");
                String nume=campuri[0];
                String an=campuri[1];
                String actori=campuri[2];
                String descriere=campuri[3];
                MovieItem movieItem=new MovieItem(nume,an,actori,descriere);
                movieItemsList.add(movieItem);
            }
        }
        finally {
            if(br!=null){
                br.close();
            }
        }
    }

    public void storeMovieItems() throws IOException{
        Path path = Paths.get(numeFisier);
        BufferedWriter bw=Files.newBufferedWriter(path);
        try {
            Iterator<MovieItem> it= movieItemsList.iterator();
            while (it.hasNext()){
                MovieItem movieItem=it.next();
                bw.write(String.format("%s\t%s\t%s\t%s",movieItem.getNume(),movieItem.getAnLansare(),movieItem.getActori(),movieItem.getDescriere()));
                bw.newLine();
            }
        }
        finally {
            if(bw!=null){
                bw.close();
            }
        }
    }

    public void saveAsTSV(Path path) throws IOException{
        BufferedWriter bw=Files.newBufferedWriter(path);
        try {
            Iterator<MovieItem> it= DatabaseStorage.getInstance().getMovieItemsList().iterator();
            while (it.hasNext()){
                MovieItem movieItem=it.next();
                bw.write(String.format("%s\t%s\t%s\t%s",movieItem.getNume(),movieItem.getAnLansare(),movieItem.getActori(),movieItem.getDescriere()));
                bw.newLine();
            }
        }
        finally {
            if(bw!=null){
                bw.close();
            }
        }
    }

}
