package com.example.melion.data;

public class PictureMetaPreview implements Comparable< PictureMetaPreview>{
    public int id;
    public int creatorId;
    public String pictureName;
    public String description;
    public double rating;
    public int numberOfRatings;
    public int isProfilePicture;

    public PictureMetaPreview(int id, int creatorId, String pictureName, String description,
                         double rating, int numberOfRatings, int isProfilePicture){
        this.id = id;
        this.creatorId = creatorId;
        this.pictureName = pictureName;
        this.description = description;
        this.rating = rating;
        this.numberOfRatings = numberOfRatings;
        this.isProfilePicture = isProfilePicture;
    }

    public int getNumberOfRatings(){
        return numberOfRatings;
    }

    @Override
    public int compareTo(PictureMetaPreview o) {
        return Integer.compare(this.getNumberOfRatings(),o.getNumberOfRatings());
    }
}
