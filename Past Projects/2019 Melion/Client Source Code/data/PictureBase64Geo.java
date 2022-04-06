package com.example.melion.data;

public class PictureBase64Geo extends PictureBase64 {
    public double latitude;
    public double longitude;
    public int time;
    public int challengeId;

    public PictureBase64Geo(int id, int creatorId, String pictureName, String description, double rating,
                     int numberOfRatings, String base64Data,
                     double latitude, double longitude, int time, int challengeId){
        super(id, creatorId, pictureName, description, rating, numberOfRatings, base64Data);
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.challengeId = challengeId;
    }

    public PictureBase64Geo(){

    }

}
