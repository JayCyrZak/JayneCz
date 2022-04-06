package com.example.melion.data;


/* TODO
    challenge
    isProfilePic
*/


import java.io.Serializable;

public class PictureBase64 implements Serializable {
    public int id;
    public int creatorId;
    public String pictureName;
    public String description;
    public double rating;
    public int numberOfRatings;
    public String base64Data;

    public PictureBase64(int id, int creatorId, String pictureName, String description,
                         double rating, int numberOfRatings, String base64Data){
        this.id = id;
        this.creatorId = creatorId;
        this.pictureName = pictureName;
        this.description = description;
        this.rating = rating;
        this.numberOfRatings = numberOfRatings;
        this.base64Data = base64Data;
    }

    public PictureBase64(){

    }

}
