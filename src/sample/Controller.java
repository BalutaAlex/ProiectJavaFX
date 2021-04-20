package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Data.DatabaseStorage;
import sample.Data.MovieItem;
import sample.Data.MovieItemStoreText;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private SortedList<MovieItem> sortedList;

    @FXML
    private ListView<MovieItem> movieItemListView;

    @FXML
    private TextArea movieDetailsTextArea;

    @FXML
    private Label dataAparitieLabel;

    @FXML
    private Label actoriLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton sortToggleButton;

    @FXML
    private TextField searchField;

    private FilteredList<MovieItem> filteredList;
    private Predicate<MovieItem> allMovies;

    public void initialize(){
        listContextMenu=new ContextMenu();
        MenuItem deleteMenuItem =new MenuItem("Delete");
        MenuItem editMenuItem=new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MovieItem item=movieItemListView.getSelectionModel().getSelectedItem();
                try {
                    deleteItem(item);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MovieItem item=movieItemListView.getSelectionModel().getSelectedItem();
                try {
                    showEditMovieDialog(item);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(editMenuItem);

        movieItemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MovieItem>() {
            @Override
            public void changed(ObservableValue<? extends MovieItem> observable, MovieItem oldValue, MovieItem newValue) {
                if(newValue!=null){
                    MovieItem item=movieItemListView.getSelectionModel().getSelectedItem();
                    movieDetailsTextArea.setText(item.getDescriere());
                    dataAparitieLabel.setText(item.getAnLansare());
                    actoriLabel.setText(item.getActori());
                }
            }
        });

        allMovies=new Predicate<MovieItem>() {
            @Override
            public boolean test(MovieItem movieItem) {
                return true;
            }
        };

        filteredList=new FilteredList<MovieItem>(DatabaseStorage.getInstance().getMovieItemsList(),allMovies);

        sortedList=new SortedList<>(filteredList, new Comparator<MovieItem>() {
            @Override
            public int compare(MovieItem o1, MovieItem o2) {
                return o1.getNume().compareTo(o2.getNume());
            }
        });

        //movieItemListView.setItems(MovieItemStoreText.getInstance().getMovieItemsList());
        movieItemListView.setItems(sortedList);
        movieItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        movieItemListView.getSelectionModel().selectFirst();

        movieItemListView.setCellFactory(new Callback<ListView<MovieItem>, ListCell<MovieItem>>() {
            @Override
            public ListCell<MovieItem> call(ListView<MovieItem> param) {
                ListCell<MovieItem> cell = new ListCell<MovieItem>() {

                    @Override
                    protected void updateItem(MovieItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getNume());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }

                        });
                return cell;
            }
        });

    }


    @FXML
    public void showNewMovieDialog() {
        Dialog<ButtonType> dialog=new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Adaugare Film Nou");
        dialog.setHeaderText("Completeaza formularul pentru a adauga un film nou");
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("movieItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Eroare");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> rezulat=dialog.showAndWait();
        if(rezulat.isPresent() && rezulat.get()==ButtonType.OK){
            DialogController controller=fxmlLoader.getController();
            MovieItem newMovie =controller.procesare();
            movieItemListView.getSelectionModel().select(newMovie);
        }
    }

    @FXML
    public void showEditMovieDialog(MovieItem item) throws SQLException {
        Dialog<ButtonType> dialog=new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Editare film");
        dialog.setHeaderText("Actualizeaza informatiile si apasa OK");
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("movieItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Eroare");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController controller=fxmlLoader.getController();

        controller.initializare(item);

        Optional<ButtonType> rezulat=dialog.showAndWait();
        if(rezulat.isPresent() && rezulat.get()==ButtonType.OK){
            MovieItem newMovie = controller.procesareEdit(item);
            movieItemListView.getSelectionModel().select(newMovie);

        }
    }

    public void deleteItem(MovieItem item) throws SQLException {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Stergere Film");
        alert.setHeaderText("Stergere film: "+item.getNume());
        alert.setContentText("Esti sigur? Apasa OK pentru a confirma sau CANCEL pentru a anula");
        Optional<ButtonType> rezultat=alert.showAndWait();

        if(rezultat.isPresent() && rezultat.get()==ButtonType.OK)
            DatabaseStorage.getInstance().removeMovieItem(item);
    }

    @FXML
    public void handleSortButton(){
        if(sortToggleButton.isSelected()){
            sortedList.setComparator(new Comparator<MovieItem>() {
                @Override
                public int compare(MovieItem o1, MovieItem o2) {
                    return o1.getAnLansare().compareTo(o2.getAnLansare());
                }
            });
        }
        else {
            sortedList.setComparator(new Comparator<MovieItem>() {
                @Override
                public int compare(MovieItem o1, MovieItem o2) {
                    return o1.getNume().compareTo(o2.getNume());
                }
            });
        }
    }

    @FXML
    public void handleSaveAs() throws IOException {
        FileChooser fileChooser=new FileChooser();
        FileChooser.ExtensionFilter extensionFilter=new FileChooser.ExtensionFilter("TSV files (.tsv)","*.tsv");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());
        if(file!=null){
            MovieItemStoreText.getInstance().saveAsTSV(Paths.get(file.getPath()));
        }
    }

    @FXML
    public void handleAbout() throws IOException {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("aboutDialog.fxml"));
            Scene scene2 = new Scene(fxml);
            Stage newDialog = new Stage();
            newDialog.setScene(scene2);
            newDialog.initModality(Modality.WINDOW_MODAL);
            newDialog.initOwner(mainBorderPane.getScene().getWindow());
            newDialog.setTitle("About");
            newDialog.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }


    public void handleSearchButton(ActionEvent actionEvent) {
        filteredList.setPredicate(new Predicate<MovieItem>() {
            @Override
            public boolean test(MovieItem movieItem) {
                return movieItem.getNume().contains(searchField.getCharacters());
            }
        });
    }
}
