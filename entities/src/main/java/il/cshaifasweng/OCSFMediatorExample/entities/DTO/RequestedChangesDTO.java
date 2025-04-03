package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;

public class RequestedChangesDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int price;
    private String ingredients;
    private String name;
    private String personalPref;
    private int dishId;

    public RequestedChangesDTO() {}

    public RequestedChangesDTO(int price, String ingredients, String name,
                               String personalPref, int dishId) {
        this.price = price;
        this.ingredients = ingredients;
        this.name = name;
        this.personalPref = personalPref;
        this.dishId = dishId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalPref() {
        return personalPref;
    }

    public void setPersonalPref(String personalPref) {
        this.personalPref = personalPref;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    @Override
    public String toString() {
        return "RequestedChangesDTO{" +
                "price=" + price +
                ", ingredients='" + ingredients + '\'' +
                ", name='" + name + '\'' +
                ", personalPref='" + personalPref + '\'' +
                ", dishId=" + dishId +
                '}';
    }
}
