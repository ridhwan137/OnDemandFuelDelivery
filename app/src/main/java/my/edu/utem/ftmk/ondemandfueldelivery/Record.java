package my.edu.utem.ftmk.ondemandfueldelivery;

import java.util.Date;

public class Record {

    private Date date;
    private String addresslatlng, typeoffuel, price;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getAddresslatlng() {
        return addresslatlng;
    }

    public void setAddresslatlng(String addresslatlng) {
        this.addresslatlng = addresslatlng;
    }

    public String getTypeoffuel() {
        return typeoffuel;
    }

    public void setTypeoffuel(String typeoffuel) {
        this.typeoffuel = typeoffuel;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
