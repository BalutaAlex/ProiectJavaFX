package sample.Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.Iterator;
import java.util.Properties;

public class DatabaseStorage {

    private static DatabaseStorage instance=new DatabaseStorage();
    private ObservableList<MovieItem> movieItemsList;

    public static DatabaseStorage getInstance(){return instance;}

    public  ObservableList<MovieItem> getMovieItemsList() { return movieItemsList; }

    public void addMovieItem(MovieItem movieItem){ movieItemsList.add(movieItem);
    }

    public void removeMovieItemFromList(MovieItem movieItem){movieItemsList.remove(movieItem);}

    public void removeMovieItem(MovieItem movieItem) throws SQLException {
        movieItemsList.remove(movieItem);
        String sql = "delete from filme where nume=?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
        preparedStatement.setString(1,movieItem.getNume());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void updateMovieItem(MovieItem newMovie,MovieItem oldMovie) throws SQLException {
        String sql = "update filme set nume=?,an=?,actori=?,descriere=? where nume=?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
        preparedStatement.setString(1,newMovie.getNume());
        preparedStatement.setString(2,newMovie.getAnLansare());
        preparedStatement.setString(3,newMovie.getActori());
        preparedStatement.setString(4,newMovie.getDescriere());
        preparedStatement.setString(5,oldMovie.getNume());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/proiectjava";
        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "");
        Connection conn = DriverManager.getConnection(dbUrl, props);
        return conn;
    }

    public void storeMovieItems (Connection connection) throws SQLException {
        String sql="insert ignore filme (nume,an,actori,descriere) values(?,?,?,?) ";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);

        try {
            Iterator<MovieItem> it= movieItemsList.iterator();
            while (it.hasNext()){
                MovieItem movieItem=it.next();
                preparedStatement.setString(1,movieItem.getNume());
                preparedStatement.setString(2,movieItem.getAnLansare());
                preparedStatement.setString(3,movieItem.getActori());
                preparedStatement.setString(4,movieItem.getDescriere());
                preparedStatement.executeUpdate();
            }
        }
        finally {
            if(connection!=null){
                connection.close();
                preparedStatement.close();
            }
        }

    }


    public void loadMovieItems(Connection connection) throws SQLException {
        movieItemsList= FXCollections.observableArrayList();
        Statement statement=connection.createStatement();
        ResultSet resultSet=statement.executeQuery("select * from filme");
        try{
            while (resultSet.next()) {
                String nume=resultSet.getString(2);
                String an=resultSet.getString(3);
                String actori=resultSet.getString(4);
                String descriere=resultSet.getString(5);
                MovieItem movieItem=new MovieItem(nume,actori,an,descriere);
                movieItemsList.add(movieItem);
            }
        }
        finally {
            if(connection!=null){
                connection.close();
                statement.close();
            }
        }
    }
}
