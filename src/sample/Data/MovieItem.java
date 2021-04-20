package sample.Data;

public class MovieItem {

    private String nume;
    private String actori;
    private String anLansare;
    private String descriere;

    public MovieItem(String nume, String actori, String anLansare,String descriere) {
        this.nume = nume;
        this.actori = actori;
        this.anLansare = anLansare;
        this.descriere=descriere;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getActori() {
        return actori;
    }

    public void setActori(String actori) {
        this.actori = actori;
    }

    public String getAnLansare() {
        return anLansare;
    }

    public void setAnLansare(String anLansare) {
        this.anLansare = anLansare;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}
