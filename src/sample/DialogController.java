package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.Data.DatabaseStorage;
import sample.Data.MovieItem;
import sample.Data.MovieItemStoreText;

import java.sql.SQLException;


public class DialogController {

    @FXML
    private TextField numeField;

    @FXML
    private TextField anField;

    @FXML
    private TextField actoriField;

    @FXML
    private TextArea descriereField;

    public void initializare(MovieItem item){
        numeField.setText(item.getNume());
        descriereField.setText(item.getDescriere());
        actoriField.setText(item.getActori());
        anField.setText(item.getAnLansare());
    }

    public MovieItem procesare(){
        String nume=numeField.getText().trim();
        String an=anField.getText().trim();
        String actori=actoriField.getText().trim();
        String descriere=descriereField.getText().trim();

        MovieItem newMovie=new MovieItem(nume,actori,an,descriere);
        DatabaseStorage.getInstance().addMovieItem(newMovie);
        return newMovie;
    }

    public MovieItem procesareEdit(MovieItem item) throws SQLException {
        String nume=numeField.getText().trim();
        String an=anField.getText().trim();
        String actori=actoriField.getText().trim();
        String descriere=descriereField.getText().trim();

        DatabaseStorage.getInstance().removeMovieItemFromList(item);

        MovieItem newMovie=new MovieItem(nume,actori,an,descriere);
        DatabaseStorage.getInstance().addMovieItem(newMovie);
        DatabaseStorage.getInstance().updateMovieItem(newMovie,item);
        return newMovie;
    }

}
